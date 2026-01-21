// fs.js 的内容
'use strict';

function readFileSync(p) {
    return p;
}

// 关键：必须赋值给 module.exports
module.exports = {
    readFileSync: readFileSync
};