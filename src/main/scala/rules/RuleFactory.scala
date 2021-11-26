package com.hayden.rules

import java.util
import java.util.stream.{IntStream}
import scala.jdk.CollectionConverters._

trait RuleFactory[A <: AbstractRule] {
  def num: Int = 256
  def rules(): List[A]
  def sequences(size: Int): java.util.List[java.util.List[Integer]]
}

trait AbstractRule {
  def sequence(initialState: Array[Int]): Iterator[Array[Int]]
  def reset(initialState: Array[Int]): Unit
  def initialState(): Array[Int]
}

class RuleFactoryImpl(num: Int=256) extends RuleFactory[RuleSequenceImpl] {
  var rulesCache: List[RuleSequenceImpl] = List()

  override def rules(): List[RuleSequenceImpl] = {
    def singleRuleSequence(i: Int): RuleSequenceImpl = {
      var theValue: String = Integer.toString(i, 2)
      val maximumLength: String = Integer.toString(num - 1, 2)
      Range(0, maximumLength.length - theValue.length).foreach(_ => theValue = "0" + theValue)
      new RuleSequenceImpl(theValue, maximumLength.length)
    }
    if(rulesCache.length == 256){
      return rulesCache
    } else {
      rulesCache = Range(0, num).map((i: Int) => {
        singleRuleSequence(i)
      }).toList
    }
    rulesCache
  }

  override def sequences(size: Int = 10): java.util.List[java.util.List[Integer]] = {
    rules().map(r => {
      val sequence = r.sequence(r.initialState())
      val innerLst: java.util.List[Integer] = IntStream.range(0, size).flatMap(_ => util.Arrays.stream(sequence.next()).map(i => i)).boxed().toList
      innerLst;
    }).asJava;
  }

}

class RuleSequenceImpl(val value: String, val maxLength: Int) extends AbstractRule {

  var prev: Array[Int] = Array(-1, -1, -1, -1, -1, -1, -1, -1);
  var sym: Array[Int] = new Array[Int](maxLength);
  val lookback: Int= 3;

  val splitted: Array[String] = value.split("");
  for((s,i) <- splitted.zipWithIndex){
      sym(i) = Integer.parseInt(s)
  }

  override def initialState(): Array[Int] = sym

  override def sequence(initialState: Array[Int]): Iterator[Array[Int]] = {
    new Iterator[Array[Int]]() {

      var state: Array[Int] = initialState;

      override def hasNext: Boolean = true

      override def next(): Array[Int] = {
        state = ruleSequence().apply();
        state;
      }
    }
  }

  override def reset(initialState: Array[Int]): Unit = {
    prev = initialState;
  }

  def nextVal(neighbors: Array[Int]): Int= {
    val toParse = new StringBuilder
    for (v <- neighbors) {
      toParse.append(v)
    }
    val i = Integer.parseInt(toParse.toString, 2)
    sym(i)
  }

  def ruleSequence(): () => Array[Int] = {
   () => {
     var inPrev: Array[Int] = null
     if(prev(0) == -1)
        inPrev = Array.copyOf(sym, sym.length)
     else
       inPrev = Array.copyOf(prev, prev.length)
     val toPrev = new Array[Int](sym.length)
     for((_,i) <- sym.zipWithIndex){
       val neighbour: Array[Int] = new Array[Int](lookback)
       var numOver = 0
       var numCopied = 0
       if(i + lookback > sym.length){
         numOver = i + lookback - sym.length
         numCopied = lookback - numOver
       }
       System.arraycopy(inPrev, i, neighbour, 0, if (i + lookback > sym.length-1) lookback - numOver else lookback)
       if (i + lookback > sym.length) System.arraycopy(inPrev, 0, neighbour, numCopied, numOver)
       toPrev(i) = nextVal(neighbour)
     }
     toPrev
   }
 }

}
