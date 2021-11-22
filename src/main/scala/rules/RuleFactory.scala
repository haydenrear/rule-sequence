package com.hayden.rules

trait RuleFactory[A <: AbstractRule] {
  def rules(): List[A];
}

trait AbstractRule {
  def sequence(initialState: Array[Integer]): Iterator[Array[Integer]]
  def reset(initialState: Array[Integer]): Unit
  def initialState(): Array[Integer]
}

class RuleFactoryImpl extends RuleFactory[RuleSequenceImpl] {
  private[rules] val num: Int = 256

  override def rules(): List[RuleSequenceImpl] = {
    Range(1, num).map((i: Int) => {
      def foo(i: Int): RuleSequenceImpl = {
        var theValue: String = Integer.toString(i, 2)
        val maximumLength: String = Integer.toString(num - 1, 2)
        Range(0,maximumLength.length-theValue.length).foreach(_ => theValue = "0"+theValue)
        new RuleSequenceImpl(theValue, maximumLength.length)
      }
      foo(i)
    }).distinct.toList
  }
}

class RuleSequenceImpl(val value: String, val maxLength: Integer) extends AbstractRule {

  var prev: Array[Integer] = Array(-1, -1, -1, -1, -1, -1, -1, -1);
  var sym: Array[Integer] = new Array[Integer](maxLength);
  val lookback: Integer = 3;

  val splitted: Array[String] = value.split("");
  for((s,i) <- splitted.zipWithIndex){
      sym(i) = Integer.parseInt(s)
  }

  override def initialState(): Array[Integer] = sym

  override def sequence(initialState: Array[Integer]): Iterator[Array[Integer]] = {
    new Iterator[Array[Integer]]() {

      var state: Array[Integer] = initialState;

      override def hasNext: Boolean = true

      override def next(): Array[Integer] = {
        state = ruleSequence().apply();
        state;
      }
    }
  }

  override def reset(initialState: Array[Integer]): Unit = {
    prev = initialState;
  }

  def nextVal(neighbors: Array[Integer]): Integer = {
    val toParse = new StringBuilder
    for (v <- neighbors) {
      toParse.append(v)
    }
    val i = Integer.parseInt(toParse.toString, 2)
    sym(i)
  }

  def ruleSequence(): () => Array[Integer] = {
   () => {
     var inPrev: Array[Integer] = null
     if(prev(0) == -1)
        inPrev = Array.copyOf(sym, sym.length)
     else
       inPrev = Array.copyOf(prev, prev.length)
     val toPrev = new Array[Integer](sym.length)
     for((_,i) <- sym.zipWithIndex){
       val neighbour: Array[Integer] = new Array[Integer](lookback)
       var numOver = 0
       var numCopied = 0
       if(i + lookback > sym.length){
         numOver = i + lookback - sym.length
         numCopied = lookback - numOver
       }
       System.arraycopy(inPrev, i, neighbour, 0, if (i + lookback > sym.length) sym.length - i else i + lookback)
       if (i + lookback > sym.length) System.arraycopy(inPrev, 0, neighbour, numCopied, numOver)
       toPrev(i) = nextVal(neighbour)
     }
     toPrev
   }
 }

}
