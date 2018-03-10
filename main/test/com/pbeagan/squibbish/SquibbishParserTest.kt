import com.pbeagan.squibbish.SquibbishParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SquibbishParserTest {
    private var parser: SquibbishParser = SquibbishParser()

    @BeforeEach
    fun setUp() {
        parser = SquibbishParser()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun forLoop1() {
        assertEquals(
                parser.parse("for [1..2]{ echo hello }").trim(),
                "for it in `seq 1 2 `; do echo hello; done;"
        )
    }

    @Test
    fun forLoop2() {
        assertEquals(
                parser.parse("for [ 1..2 ]{ echo hello; }").trim(),
                "for it in `seq 1 2 `; do echo hello; done;"
        )
    }

    @Test
    fun forLoop3() {
        assertEquals(
                parser.parse("for[1 .. 2]{ echo hello }").trim(),
                "for it in `seq 1 2 `; do echo hello; done;"
        )
    }

    @Test
    fun forLoop4() {
        assertEquals(
                parser.parse("""
                    for [1..2]{
                       echo hello
                    }
                    """.trimMargin()).trim(),
                "for it in `seq 1 2 `; do echo hello; done;"
        )
    }

    @Test
    fun forLoop5() {
        assertEquals(
                parser.parse("for [1..2]{ echo hello }").trim(),
                "for it in `seq 1 2 `; do echo hello; done;"
        )
    }

    @Test
    fun forLoop6() {
        assertEquals(
                parser.parse("for [1..2]{ echo \$it }").trim(),
                "for it in `seq 1 2 `; do echo \$it; done;"
        )
    }

    @Test
    fun nestedForLoop1() {
        assertEquals(
                parser.parse("for [1..2]{ for [10..20] { echo hello} }").trim(),
                "for it in `seq 1 2 `; do for it in `seq 10 20 `; do echo hello; done; done;"
        )
    }

    @Test
    fun branch1() {
        assertEquals(
                parser.parse("branch { [ 0 = 1 ] { echo true } }").trim(),
                "if [ 0 = 1 ] ; then echo true; elif [ 1 ]; then :; fi;"
        )
    }

    @Test
    fun branch2() {
        assertEquals(
                parser.parse("for [1..5]{branch { [ \$it = 1 ] { echo true } [ true ] {echo \$it} } }").trim(),
                "for it in `seq 1 5 `; do if [ \$it = 1 ] ; then echo true; elif [ true ] ; then echo \$it; elif [ 1 ]; then :; fi; done;"
        )
    }

    @Test
    fun branch3() {
        assertEquals(
                parser.parse("""
                    for [1..5] {
                        branch {
                            [ ${"$"}it = 1 ] {
                                echo true
                            }
                            [ true ] {
                                echo ${"$"}it
                            }
                        }
                    }""".trimIndent()).trim(),
                "for it in `seq 1 5 `; do if [ \$it = 1 ] ; then echo true; elif [ true ] ; then echo \$it; elif [ 1 ]; then :; fi; done;"
        )
    }

    @Test
    fun branch4() {
        assertEquals(
                parser.parse("for [1..5..2]{branch { [ \$it = 1 ] { echo true } [ true ] {echo \$it} } }").trim(),
                "for it in `seq 1..5 2 `; do if [ \$it = 1 ] ; then echo true; elif [ true ] ; then echo \$it; elif [ 1 ]; then :; fi; done;"
        )
    }

    @Test
    fun branch5() {
        assertEquals(
                parser.parse("""
                    branch {
                     [ 0 = 1 ] {
                        branch{
                            [ 1 ] {
                                echo hello
                            }
                        }
                     }
                     [ true ] {
                        echo hello
                      }
                    }""").trim(),
                "if [ 0 = 1 ] ; then if [ 1 ] ; then echo hello; elif [ 1 ]; then :; fi; elif [ true ] ; then echo hello; elif [ 1 ]; then :; fi;"
        )
    }

    @Test
    fun bash() {
        assertEquals(parser.parse("bash { cd ..; pwd; cd -; }").trim(), "cd .. ; pwd ; cd - ;")
    }

    @Test
    fun bash2() {
        assertEquals(
                parser.parse("for [1..3] { bash { cd ..; pwd; cd -; }}").trim(),
                "for it in `seq 1 3 `; do cd .. ; pwd ; cd - ; done;"
        )
    }

    @Test
    fun func1() {
        assertEquals(
                parser.parse("fn myfunction = { a | echo \$a }").trim(),
                "for it in `seq 1 3 `; do cd .. ; pwd ; cd - ; done;"
        )
    }

    @Test
    fun func2() {
        assertEquals(
                parser.parse("fn myfunction = { a | for [1..5] { br { [ \$a = \$it ] { echo \$a }  [ 1 ] { echo false \$a} }}}").trim(),
                "for it in `seq 1 3 `; do cd .. ; pwd ; cd - ; done;"
        )
    }

    @Test
    fun func3() {
        assertEquals(
                parser.parse("""
fn myfunction = { a b c |
    for 1..5 {
        br {
            | ${"$"}a = ${"$"}it |
            echo ${"$"}a
            | 1 |
            echo false ${"$"}a
        }
    }
}""").trim(), "")
    }
}