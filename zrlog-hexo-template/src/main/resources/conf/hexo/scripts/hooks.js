(function() {
    // 1. 提取原生方法引用，确保补丁内部使用的是“干净”的版本
    const Array_slice = Array.prototype.slice;
    const Array_filter = Array.prototype.filter;
    const Array_sort = Array.prototype.sort;
    const Array_forEach = Array.prototype.forEach;
    const Array_find = Array.prototype.find;

    // 2. 核心包装函数：不直接修改 Array.prototype 里的方法行为
    // 而是通过逻辑判断来避免无限递归
    function wrap(arr) {
        if (!arr || arr._isWrapped) return arr;
        const newArr = Array.from(arr);
        Object.defineProperty(newArr, '_isWrapped', { value: true });
        Object.defineProperty(newArr, 'data', { get: function() { return this; } });
        return newArr;
    }

    // 3. 覆盖 find
    Array.prototype.find = function(query) {
        // 如果是正常的函数回调，直接用原生 find
        if (typeof query === 'function') {
            return Array_find.call(this, query);
        }

        // 处理 Hexo 对象的查询 { parent: ... }
        const list = Array.from(this);
        const result = Array_filter.call(list, item => {
            if (!query) return true;
            return Object.keys(query).every(key => {
                const qVal = query[key];
                // 处理 $exists: false
                if (qVal && typeof qVal === 'object' && qVal.$exists === false) {
                    return item[key] == null;
                }
                // 处理普通的相等判断 (比如 parent: cat._id)
                return item[key] === qVal;
            });
        });
        return wrap(result);
    };

    // 4. 覆盖 sort
    Array.prototype.sort = function(field) {
        if (typeof field === 'function' || typeof field === 'undefined') {
            return Array_sort.call(this, field);
        }
        const sortField = String(field);
        const arr = Array.from(this);
        const sorted = Array_sort.call(arr, (a, b) => {
            const vA = (a && a[sortField] != null) ? a[sortField] : '';
            const vB = (b && b[sortField] != null) ? b[sortField] : '';
            return vA > vB ? 1 : (vA < vB ? -1 : 0);
        });
        return wrap(sorted);
    };

    // 5. 补全其他
    Array.prototype.each = function(fn) {
        return Array_forEach.call(Array.from(this), fn);
    };

    if (!Array.prototype.limit) {
        Array.prototype.limit = function(n) {
            // 确保 n 是数字
            const limitNum = parseInt(n, 10);
            if (isNaN(limitNum)) return this;

            // 使用 slice 从索引 0 开始截取到 n
            return this.slice(0, limitNum);
        };
    }

    Array.prototype.findOne = function(query) {
        const res = this.find(query);
        return (res && res.length > 0) ? res[0] : null;
    };
})();