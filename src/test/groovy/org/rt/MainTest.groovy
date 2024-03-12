package org.rt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class MainTest {
    @Test
    void parseTableTest() {
        String table = """|a| b| c |
                       |dsq|ddddh|       21|"""
        String parsedTable = MdTableParser.parseTable(table).get()

        Assertions.assertEquals("""| a   | b     | c  |
| dsq | ddddh | 21 |""", parsedTable)
    }

    @Test
    void parseTableTest2() {
        String table = """|a| b| c |
                       |dsq|ddddh|       21|
                       ||||"""
        String parsedTable = MdTableParser.parseTable(table).get()

        Assertions.assertEquals("""| a   | b     | c  |
| dsq | ddddh | 21 |
|     |       |    |""", parsedTable)
    }

    @Test
    void parseTableTest3() {
        String table = """|ajjjj |
                       |dsq1|"""
        String parsedTable = MdTableParser.parseTable(table).get()

        Assertions.assertEquals("""| ajjjj |
| dsq1  |""", parsedTable)
    }

    @Test
    void parseTableTest4() {
        String table = """ |   a| b| c  |        
                         |dsq|ddddh|       21|          """
        String parsedTable = MdTableParser.parseTable(table).get()

        Assertions.assertEquals("""| a   | b     | c  |
| dsq | ddddh | 21 |""", parsedTable)
    }

    @Test
    void parseTableTest5() {
        String table = """ ||||        
                         ||| |          """
        String parsedTable = MdTableParser.parseTable(table).get()

        Assertions.assertEquals("""|  |  |  |
|  |  |  |""", parsedTable)
    }


    @Test
    void parseTableTest6() {
        String table = """ |||r|        
                         |ev|| |          """
        String parsedTable = MdTableParser.parseTable(table).get()

        Assertions.assertEquals("""|    |  | r |
| ev |  |   |""", parsedTable)
    }

    @Test
    void parseInvalidTableTest() {
        String table = """|a| b| c |
                       |dsq|ddddh       21|"""
        var parsedTable = MdTableParser.parseTable(table)

        Assertions.assertTrue(parsedTable.isEmpty())
    }

    @Test
    void parseInvalidTableTest2() {
        String table = """|a| b| c |
                       """
        var parsedTable = MdTableParser.parseTable(table)

        Assertions.assertTrue(parsedTable.isEmpty())
    }

    @Test
    void parseFile() {
        File file = new File("./src/test/resources/example.md")
        File outputFile = new File("./src/test/resources/result.md")
        MdTableParser.parseMdFile(file, outputFile);
        Assertions.assertEquals((new File("./src/test/resources/example-parsed.md")).readLines(), outputFile.readLines())
    }
}