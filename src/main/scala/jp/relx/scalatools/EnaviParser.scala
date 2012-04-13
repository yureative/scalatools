package jp.relx.scalatools.enavi

import java.io.File
import scala.io.Source
import scala.annotation.tailrec
import java.util.Currency
import java.text.NumberFormat
import java.util.Locale

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

    rec(Nil, expenses.sortBy(_.item)).
      sortBy(_.amount)(Ordering[Double].reverse)
  }
}

class EnaviDecorator(expenses: List[Expense]) {
  def toHtml =
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8"/>
        <style type="text/css">
          <![CDATA[
            h1 {
                font-size: 115%;
            }
            table,
            th,
            td {
                border: solid 1px;
                border-collapse: collapse;
            }
            tr.even {
                background-color: #DFEFFF;
            }
            th {
                background-color: #DDDDDD;
            }
            td.amount {
                text-align: right;
            }
            p.totalAmount {
                font-size: 110%;
            }
          ]]>
        </style>
      </head>
      <body>
        {getTable}
        <p class="totalAmount">合計支払額: {totalAmount(expenses)}</p>
      </body>
    </html>

  private def getTable =
    <table>
      <tr>
        <th>項目</th>
        <th>金額</th>
      </tr>
      {for ((expense, idx) <- expenses.zipWithIndex) yield
      <tr class={if (idx % 2 == 0) "even" else "odd"}>
        <td class="item">{expense.item}</td>
        <td class="amount">{toCurrency(expense.amount)}</td>
      </tr>
      }
    </table>

  private def toCurrency(number: Double) = {
    lazy val formatter = NumberFormat.getCurrencyInstance(Locale.JAPAN)
    formatter.format(number)
  }

  private def totalAmount(expenses: List[Expense]) =
    toCurrency(expenses.foldLeft(0.0)(_ + _.amount))
}
