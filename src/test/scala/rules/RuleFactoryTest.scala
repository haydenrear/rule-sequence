package com.hayden.rules

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{BeforeEach, Test}

class RuleFactoryTest {

  val ruleFactory: RuleFactory[RuleSequenceImpl] = new RuleFactoryImpl
  var rules: List[RuleSequenceImpl] = List()

  @BeforeEach
  def beforeEach(): Unit = {
    rules = ruleFactory.rules()
  }

  @Test
  def testRuleFactoryCreateRuleSequence(): Unit = {
    assertThat(rules.length).isNotZero()
  }

  @Test
  def testCreateRuleSequence(): Unit = {
     assertThat(rules.length).isEqualTo(255)
    rules.foreach(r => {
      val sequence = r.sequence(r.initialState())
      println(r.initialState().mkString("Array(", ", ", ")"))
      Range(0,10).map(_ => println(sequence.next().mkString("Array(", ", ", ")")))
    })
  }
}
