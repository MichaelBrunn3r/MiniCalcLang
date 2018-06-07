package io.github.orangeutan.minicalc

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.net.URL

class MiniCalcParser {
    companion object {
        @JvmStatic

        fun parse(code: String, savePos: Boolean = true): MiniCalcFile {
            return MiniCalcAntlrParser(CommonTokenStream(MiniCalcAntlrLexer(CharStreams.fromString(code))))
                    .miniCalcFile().toAST(savePos)
        }

        fun parseResource(url: URL, savePos: Boolean = true): MiniCalcFile {
            return MiniCalcAntlrParser(CommonTokenStream(MiniCalcAntlrLexer(CharStreams.fromStream(url.openStream()))))
                    .miniCalcFile().toAST(savePos)
        }

        fun parseResource(resName: String, savePos: Boolean = true): MiniCalcFile {
            return parseResource(MiniCalcParser::class.java.classLoader.getResource(resName), savePos)
        }
    }
}