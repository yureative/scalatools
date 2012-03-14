package jp.relx.scalatools.enavi
import java.io.File
import scala.io.Source
import scala.annotation.tailrec

object EnaviParser {
  def fromCsv(csv: File): EnaviParser = {
    new CsvEnaviParser(csv)
  }
}

case class Expense(item: String, amount: Double)

trait EnaviParser {
  def parse(): List[Expense]
}

class CsvEnaviParser(csv: File) extends EnaviParser {
  val CSV_CHARSET   = "Windows-31J"
  val CSV_SEPARATOR = ","

  def parse(): List[Expense] = {
    val s = Source.fromFile(csv, CSV_CHARSET)
    try {
      val expenses = s.getLines() drop(1) map(getLineParser)
      return summarise(expenses.toList)
    } finally {
      s.close()
    }
  }

  private def getLineParser = (line: String) => {
    val splited = line.split(CSV_SEPARATOR)
    Expense(splited(3).trim(), splited(5).toDouble)
  }
  
  private def summarise(expenses: List[Expense]): List[Expense] = {
    @tailrec
    def rec(acc: List[Expense], exps: List[Expense]): List[Expense] = {
      exps match {
        case Nil => acc
        case x::xs if xs != Nil && x.item == xs.head.item =>
          rec(acc, Expense(x.item, x.amount + xs.head.amount) :: xs.tail)
        case x::xs =>
          rec(x :: acc, xs)
      }
    }

    rec(Nil, expenses.sortBy(_.item))
  }
}

class EnaviDecorator(expenses: List[Expense]) {
  def toHtml =
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8"/>
      </head>
      <body>
      {getTable}
      </body>
    </html>

  private def getTable =
    <table>
      <tr>
        <th>項目</th>
        <th>金額</th>
      </tr>
      {for (expense <- expenses) yield
      <tr>
        <td>{expense.item}</td>
        <td>{expense.amount}</td>
      </tr>
      }
    </table>
  
}
