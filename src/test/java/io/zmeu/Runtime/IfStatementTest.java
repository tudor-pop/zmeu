package io.zmeu.Runtime;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class IfStatementTest extends BaseTest {

    @Test
    void consequentLess() {
        var res = eval("""
                {
                    var x = 1
                    var y = 2
                    if (x < y){
                        x = y 
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(2, res);
    }

    @Test
    void consequentLessEq() {
        var res = eval("""
                {
                    var x = 2
                    var y = 2
                    if (x <= y){
                        x = y 
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(2, res);
    }

    @Test
    void consequentGreat() {
        var res = eval("""
                {
                    var x = 3
                    var y = 2
                    if (x > y){
                        x = y 
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(2, res);
    }

    @Test
    void consequentGreatEq() {
        var res = eval("""
                {
                    var x = 2
                    var y = 2
                    if (x >= y){
                        x = 3 
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(3, res);
    }

    @Test
    void alternateGreat() {
        var res = eval("""
                {
                    var x = 1
                    var y = 2
                    if (x > y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(3, res);
    }

    @Test
    void alternateGreatEq() {
        var res = eval("""
                {
                    var x = 1
                    var y = 2
                    if (x >= y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(3, res);
    }

    @Test
    void alternateEq() {
        var res = eval("""
                {
                    var x = 2
                    var y = 1
                    if (x <= y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(3  , res);
    }

    @Test
    void alternate() {
        var res = eval("""
                {
                    var x = 2
                    var y = 1
                    if (x < y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """);
        log.warn(toJson(res));
        assertEquals(3, res);
    }


}
