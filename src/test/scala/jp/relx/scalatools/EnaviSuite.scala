package jp.relx.scalatools.enavi

import java.io.File
import java.io.FileWriter

import org.scalatest.FunSuite

class EnaviSuite extends FunSuite {
  val INPUT_CSV    = new File("/path/to/enavi.csv")
  val OUTPUT_XHTML = new File("enavi.xhtml")

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
