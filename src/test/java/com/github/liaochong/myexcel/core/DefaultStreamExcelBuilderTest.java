package com.github.liaochong.myexcel.core;

import com.github.liaochong.myexcel.core.pojo.CommonPeople;
import com.github.liaochong.myexcel.utils.FileExportUtil;
import com.github.liaochong.myexcel.utils.TempFileOperator;
import com.sun.tools.javac.util.Assert;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

/**
 * @author liaochong
 * @version 1.0
 */
class DefaultStreamExcelBuilderTest extends BasicTest {

    @Test
    void commonBuild() throws Exception {
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .start()) {
            data(excelBuilder, 10000);
            Workbook workbook = excelBuilder.build();
            FileExportUtil.export(workbook, new File(TEST_DIR + "common_build.xlsx"));
        }
    }

    @Test
    void hasStyleBuild() throws Exception {
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .hasStyle()
                .start()) {
            data(excelBuilder, 10000);
            Workbook workbook = excelBuilder.build();
            FileExportUtil.export(workbook, new File(TEST_DIR + "has_style_build.xlsx"));
        }
    }

    @Test
    void customWidthBuild() throws Exception {
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .hasStyle()
                .widths(15, 20, 25, 30)
                .start()) {
            data(excelBuilder, 10000);
            Workbook workbook = excelBuilder.build();
            FileExportUtil.export(workbook, new File(TEST_DIR + "custom_width_build.xlsx"));
        }
    }

    @Test
    void continueBuild() throws Exception {
        DefaultStreamExcelBuilder excelBuilder = null;
        try {
            excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                    .fixedTitles()
                    .hasStyle()
                    .widths(15, 20, 25, 30)
                    .start();
            data(excelBuilder, 10000);
            Workbook workbook = excelBuilder.build();

            excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class, workbook)
                    .fixedTitles()
                    .hasStyle()
                    .start();
            data(excelBuilder, 10000);
            FileExportUtil.export(workbook, new File(TEST_DIR + "continue_build.xlsx"));
        } catch (Throwable e) {
            if (excelBuilder != null) {
                excelBuilder.clear();
            }
            throw new RuntimeException(e);
        }
    }

    @Test
    void cancelBuild() throws Exception {
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .hasStyle()
                .widths(15, 20, 25, 30)
                .start()) {
            data(excelBuilder, 10000);
            excelBuilder.cancle();
        }
    }

    @Test
    void buildAsPaths() throws Exception {
        List<Path> paths = null;
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .hasStyle()
                .widths(15, 20, 25, 30)
                .capacity(1000)
                .start()) {
            data(excelBuilder, 10000);
            paths = excelBuilder.buildAsPaths();
            Assert.check(paths.size() == 11);
        } finally {
            TempFileOperator.deleteTempFiles(paths);
        }
    }

    @Test
    void buildAsZip() throws Exception {
        Path zip = null;
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .hasStyle()
                .widths(15, 20, 25, 30)
                .capacity(1000)
                .start()) {
            data(excelBuilder, 10000);
            zip = excelBuilder.buildAsZip("test");
        } finally {
            TempFileOperator.deleteTempFile(zip);
        }
    }

    @Test
    void bigBuild() throws Exception {
        try (DefaultStreamExcelBuilder excelBuilder = DefaultStreamExcelBuilder.of(CommonPeople.class)
                .fixedTitles()
                .hasStyle()
                .widths(15, 20, 25, 30)
                .start()) {
            data(excelBuilder, 1200000);
            Workbook workbook = excelBuilder.build();
            FileExportUtil.export(workbook, new File(TEST_DIR + "big_build.xlsx"));
        }
    }

    private void data(DefaultStreamExcelBuilder excelBuilder, int size) {
        BigDecimal oddMoney = new BigDecimal(109898);
        BigDecimal evenMoney = new BigDecimal(66666);
        for (int i = 0; i < size; i++) {
            CommonPeople commonPeople = new CommonPeople();
            boolean odd = i % 2 == 0;
            commonPeople.setName(odd ? "张三" : "李四");
            commonPeople.setAge(odd ? 18 : 24);
            commonPeople.setDance(odd ? true : false);
            commonPeople.setMoney(odd ? oddMoney : evenMoney);
            excelBuilder.append(commonPeople);
        }
    }
}