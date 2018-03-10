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
                "for it in `seq 1 2 `; do echo hello; done;",
                parser.parse("for 1..2{ echo hello }").trim()
        )
    }

    @Test
    fun forLoop2() {
        assertEquals(
                "for it in `seq 1 2 `; do echo hello; done;",
                parser.parse("for  1..2 { echo hello; }").trim()
        )
    }

    @Test
    fun forLoop3() {
        assertEquals(
                "for it in `seq 1 2 `; do echo hello; done;",
                parser.parse("for 1 .. 2{ echo hello }").trim()
        )
    }

    @Test
    fun forLoop4() {
        assertEquals(
                "for it in `seq 1 2 `; do echo hello; done;",
                parser.parse("""
                    for 1..2{
                       echo hello
                    }
                    """.trimMargin()).trim())
    }

    @Test
    fun forLoop5() {
        assertEquals(
                "for it in `seq 1 2 `; do echo hello; done;",
                parser.parse("for 1..2{ echo hello }").trim()
        )
    }

    @Test
    fun forLoop6() {
        assertEquals(
                "for it in `seq 1 2 `; do echo \$it; done;",
                parser.parse("for 1..2{ echo \$it }").trim()
        )
    }

    @Test
    fun nestedForLoop1() {
        assertEquals(
                "for it in `seq 1 2 `; do for it in `seq 10 20 `; do echo hello; done; done;",
                parser.parse("for 1..2{ for 10..20 { echo hello} }").trim()
        )
    }

    @Test
    fun branch1() {
        assertEquals(
                "if [ 0 = 1 ]; then echo true; elif [ 1 ]; then :; fi;",
                parser.parse("branch {  0 = 1  { echo true } }").trim()
        )
    }

    @Test
    fun branch2() {
        assertEquals(
                "for it in `seq 1 5 `; do if [ \$it = 1 ]; then echo true; elif [ true ]; then echo \$it; elif [ 1 ]; then :; fi; done;",
                parser.parse("for 1..5{branch { \$it = 1  { echo true }  true  {echo \$it} } }").trim())
    }

    @Test
    fun branch3() {
        assertEquals(
                "for it in `seq 1 5 `; do if [ \$it = 1 ]; then echo true; elif [ true ]; then echo \$it; elif [ 1 ]; then :; fi; done;",
                parser.parse("""
                    for 1..5 {
                        branch {
                             ${"$"}it = 1  {
                                echo true
                            }
                            true  {
                                echo ${"$"}it
                            }
                        }
                    }""".trimIndent()).trim()
        )
    }

    @Test
    fun branch4() {
        assertEquals(
                "for it in `seq 1..5 2 `; do if [ \$it = 1 ]; then echo true; elif [ true ]; then echo \$it; elif [ 1 ]; then :; fi; done;",
                parser.parse("for 1..5..2{branch {  \$it = 1  { echo true } true  {echo \$it} } }").trim()
        )
    }

    @Test
    fun branch5() {
        assertEquals(
                "if [ 0 = 1 ]; then if [ 1 ]; then echo hello; elif [ 1 ]; then :; fi; elif [ true ]; then echo hello; elif [ 1 ]; then :; fi;",
                parser.parse("""
                    branch {
                     0 = 1 {
                        branch{
                             1  {
                                echo hello
                            }
                        }
                     }
                      true  {
                        echo hello
                      }
                    }""").trim()
        )
    }

    @Test
    fun bash() {
        assertEquals("cd .. ; pwd ; cd - ;", parser.parse("bash { cd ..; pwd; cd -; }").trim())
    }

    @Test
    fun bash2() {
        assertEquals("for it in `seq 1 3 `; do cd .. ; pwd ; cd - ; done;",
                parser.parse("for 1..3 { bash { cd ..; pwd; cd -; }}").trim()
        )
    }

    @Test
    fun func1() {
        assertEquals(
                "myfunction () { local a=\"\${1}\"; echo \$a; };",
                parser.parse("fn myfunction = { a | echo \$a }").trim()
        )
    }

    @Test
    fun func2() {
        assertEquals(
                "myfunction () { local a=\"\${1}\"; for it in `seq 1 5 `; do if [ \$a = \$it ]; then echo \$a; elif [ 1 ]; then echo false\$a; elif [ 1 ]; then :; fi; done; };",
                parser.parse("fn myfunction = { a | for 1..5 { br {  \$a = \$it  { echo \$a }   1  { echo false \$a} }}}").trim()
        )
    }

    @Test
    fun func3() {
        assertEquals(
                "myfunction () { local a=\"\${1}\"; local b=\"\${2}\"; local c=\"\${3}\"; for it in `seq 1 5 `; do if [ \$a = \$it ]; then echo \$a; elif [ 1 ]; then echo false\$a; elif [ 1 ]; then :; fi; done; };",
                parser.parse("""
fn myfunction = { a b c |
    for 1..5 {
        br {
            ${"$"}a = ${"$"}it {
            echo ${"$"}a
            } 1 {
            echo false ${"$"}a
            }
        }
    }
}""").trim())
    }

    @Test
    fun case1() {
        assertEquals(
                "myfunction () { local a=\"\${1}\"; for it in `seq 1 5 `; do if [ \$a = \$it ]; then echo \$a; elif [ 1 ]; then echo false\$a; elif [ 1 ]; then :; fi; done; };",
                parser.parse("br 1 {  2  { echo \$a }   1  { echo false \$a} } ").trim()
        )
    }
}