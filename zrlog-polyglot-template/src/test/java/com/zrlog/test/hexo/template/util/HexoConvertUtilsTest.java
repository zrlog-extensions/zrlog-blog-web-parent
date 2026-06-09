package com.zrlog.test.hexo.template.util;

import com.zrlog.blog.hexo.template.util.HexoConvertUtils;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.data.dto.ArticleDetailDTO;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HexoConvertUtilsTest {

    @Test
    public void shouldReturnDisabledWhenPrevOrNextMissing() {
        ArticleDetailPageVO pageVO = new ArticleDetailPageVO();
        pageVO.setLog(new ArticleDetailDTO());
        assertEquals(Map.of("enable", false), HexoConvertUtils.getPrevLog(pageVO));
        assertEquals(Map.of("enable", false), HexoConvertUtils.getNextLog(pageVO));
    }

    @Test
    public void shouldReturnMappedPrevAndNextLog() {
        ArticleDetailDTO detailDTO = new ArticleDetailDTO();
        ArticleDetailDTO.LastLogDTO lastLogDTO = new ArticleDetailDTO.LastLogDTO();
        lastLogDTO.setTitle("Prev");
        lastLogDTO.setUrl("/prev");
        detailDTO.setLastLog(lastLogDTO);
        ArticleDetailDTO.NextLogDTO nextLogDTO = new ArticleDetailDTO.NextLogDTO();
        nextLogDTO.setTitle("Next");
        nextLogDTO.setUrl("/next");
        detailDTO.setNextLog(nextLogDTO);

        ArticleDetailPageVO pageVO = new ArticleDetailPageVO();
        pageVO.setLog(detailDTO);

        assertEquals("Prev", HexoConvertUtils.getPrevLog(pageVO).get("title"));
        assertEquals("/next", HexoConvertUtils.getNextLog(pageVO).get("path"));
        assertFalse(HexoConvertUtils.getPrevLog(pageVO).containsKey("enable"));
    }
}
