package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.ModuleExpression;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ModuleTest extends BaseTest {

    @Test
    void moduleUnquoted() {
        var res = parse("module Backend api {}");
        var expected = program(ModuleExpression.module("Backend", "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void moduleQuoted() {
        var res = parse("module 'Backend' api {}");
        var expected = program(ModuleExpression.module("'Backend'", "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void moduleQuotedDouble() {
        var res = parse("""
                module "Backend" api {}
                """);
        var expected = program(ModuleExpression.module("""
                "Backend"
                """, "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void moduleProviderNamespacedQuoted() {
        var res = parse("module 'Aws.Storage' api {}");
        var expected = program(ModuleExpression.module("'Aws.Storage'", "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void moduleProviderNamespacedUnquoted() {
        var res = parse("module Aws.Storage api {}");
        var expected = program(ModuleExpression.module("Aws.Storage", "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    /*
     * import S3 from 'Aws.Storage'
     *
     * resource S3.Bucket prod {
     *
     * }
     *
     * */
    @Test
    void moduleProviderWithResourceNamespaced() {
        var res = parse("module 'Aws.Storage/S3.Bucket' api {}");
        var expected = program(ModuleExpression.module("'Aws.Storage/S3.Bucket'", "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void moduleProviderResourceNamespacedWithDate() {
        var res = parse("module 'Aws.Storage/S3.Bucket@2022-01-20' api {}");
        var expected = program(ModuleExpression.module("'Aws.Storage/S3.Bucket@2022-01-20'", "api", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void moduleWithBody() {
        var res = parse("""
                module 'Aws.Storage/S3.Bucket@2022-01-20' api {
                    name = "bucket-prod"
                }
                """);
        var expected = program(ModuleExpression.module("'Aws.Storage/S3.Bucket@2022-01-20'", "api",
                block(assign("name", "bucket-prod"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
