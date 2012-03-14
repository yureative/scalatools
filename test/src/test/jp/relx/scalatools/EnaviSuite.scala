package test.jp.relx.scalatools
import org.scalatest.FunSuite
import jp.relx.scalatools.enavi.EnaviParser
import java.io.File
import jp.relx.scalatools.enavi.EnaviDecorator
import java.io.FileWriter
import java.nio.channels.FileChannel

class EnaviSuite extends FunSuite {
  val INPUT_CSV    = new File("/path/to/enavi.csv")
  val OUTPUT_XHTML = new File("/tmp/enavi.xhtml")

  test("parse csv") {
    val expenses = EnaviParser.fromCsv(INPUT_CSV).parse()
    expenses foreach println
  }

  test("parse csv and render xhtml") {
    val expenses = EnaviParser.fromCsv(INPUT_CSV).parse()
    val html = new EnaviDecorator(expenses).toHtml
    val out = new FileWriter(OUTPUT_XHTML)
    try {
      out.write(html.toString())
    } finally {
      out.close()
    }
  }

}