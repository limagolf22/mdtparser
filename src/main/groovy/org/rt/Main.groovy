package org.rt

class MdTableParser {
    static void parseMdFile(File file, File outputFile) {
        List<String> lines = file.readLines()
        def writer = outputFile.newWriter()

        boolean isUpdating = false
        List<String> parsedTable = []
        lines.each { l ->
            if (l.trim().startsWith('|')) {
                if (isUpdating) {
                    parsedTable += l
                } else {
                    parsedTable = [l]
                    isUpdating = true
                }
            } else {
                if (isUpdating) {
                    isUpdating = false
                    parseTable(parsedTable).orElse(parsedTable).forEach { it -> writer.writeLine(it) }
                }
                writer.writeLine(l)
            }
        }
        if (isUpdating) {
            parseTable(parsedTable).orElse(parsedTable).forEach { it -> writer.writeLine(it) }
        }
        writer.flush()
        writer.close()
    }


    static Optional<List<String>> parseTable(List<String> table) {
        var rows = table.collect(it -> it.trim())
        if (rows.size() == 0 || rows[0].count('|') < 2) {
            return Optional.empty()
        }
        int columnCount = rows[0].count('|') - 1

        if (isHeaderUnderline(rows)){
            rows[1] = '|'
            (0..columnCount-1).each(i -> {
                rows[1] += ' - |'
            }
            )
        }

        var maxSizes = (1..columnCount).collect { it -> 0 }
        for (line in rows) {
            if (isValidRow(line, columnCount)) {
                line.substring(1, line.length() - 1).split("\\|", -1).eachWithIndex { String entry, int i ->
                    maxSizes[i] = Math.max(entry.trim().length(), maxSizes[i])
                }
            } else {
                return Optional.empty()
            }
        }
        List<String> parsedTable = []
        for (line in rows) {
            String newLine = "|"
            line.substring(1, line.length() - 1).split("\\|", -1).eachWithIndex { String entry, int i ->
                newLine += " "
                newLine += (entry.trim() + produceChain(maxSizes[i], " ")).substring(0, maxSizes[i])
                newLine += " |"
            }
            parsedTable += newLine
        }
        if (isHeaderUnderline(rows)) {
            parsedTable[1] = '|'
            (0..columnCount-1).each(i -> {
                parsedTable[1] += ' '
                parsedTable[1] += produceChain(maxSizes[i], "-")
                parsedTable[1] += ' |'
            }
            )
        }
        return Optional.of(parsedTable)
    }


    static Optional<String> parseTable(String table) {
        return parseTable(Arrays.asList(table.split('\n'))).map(it -> it.join('\n'))
    }

    static def isValidRow(String line, int columns) {
        return line.startsWith('|') && line.endsWith('|') && columns == (line.count('|') - 1)
    }

    static def isHeaderUnderline(List<String> rows){
        return rows.size() > 1 && rows[1].matches(/([| -]+)/) && rows[1].contains("-")
    }

    static String produceChain(int number, String s) {
        String res = ""
        for (i in 0..<number) {
            res += s
        }
        return res
    }
}