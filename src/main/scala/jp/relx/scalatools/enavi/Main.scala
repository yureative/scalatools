package jp.relx.scalatools.enavi
import java.io.File
import org.apache.commons.io.FileUtils

object Main {
  def main(args: Array[String]) {
    require(args.length > 0)
    
    args.head match {
      case "parse" => {
        require(args.length == 3)

        val csv = new File(args(1))
        require(csv.isFile())
        
        val parser = EnaviParser.fromCsv(csv)
        val html = new EnaviDecorator(parser.parse()).toHtml

        FileUtils.writeStringToFile(new File(args(2)), html.toString)
      }
      case op => throw new UnsupportedOperationException(op)
    }
  }
}