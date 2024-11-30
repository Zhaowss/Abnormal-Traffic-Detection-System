package com.company.encryptedtrafficclassifier.common.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;
import java.sql.Types;

public class SystemCodeGenerateUtil {

    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/encrypted_traffic_classifier?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
        String username = "root";
        String password = "root";
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .enableSwagger()
                        .disableOpenDir()
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                )
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder -> builder
                        .parent("com.company.encryptedtrafficclassifier")
                        .entity("entity")
                        .mapper("mapper")
                        .xml("mapper.xml")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                )
                .strategyConfig(builder -> builder
                        .addInclude("result")
                        .entityBuilder()
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .controllerBuilder()
                        .enableRestStyle()
                        .formatFileName("%sController")
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .mapperBuilder()
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}