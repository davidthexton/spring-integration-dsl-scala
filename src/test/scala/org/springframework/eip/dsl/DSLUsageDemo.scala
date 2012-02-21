package org.springframework.eip.dsl

import org.junit.{Assert, Test}
import org.springframework.eip.dsl.DSL._
import org.springframework.integration.Message
import org.springframework.integration.message.GenericMessage

class DSLUsageDemo {

  @Test
  def simpleCompositionTestSend = {
    val messageFlow = 
      transform.using{m:Message[String] => m.getPayload().toUpperCase()} -->
      handle.using{m:Message[_] => println(m)}
      
    messageFlow.send("hello")
    println("done")
  }
  
  @Test
  def simpleCompositionTestSendAndReceive = {
    val messageFlow = 
      transform.using{m:Message[String] => m.getPayload().toUpperCase()} -->
      handle.using{m:Message[_] => println(m);m}
      
    val reply = messageFlow.sendAndReceive[String]("hello")
    println(reply)
  }
  
  @Test
  def simpleCompositionTestSendWithPubSubChannel = {
    val messageFlow = 
      transform.using{m:Message[String] => m.getPayload().toUpperCase()} -->
      PubSubChannel("pubSub") --< (
         transform.using{m:Message[_] => m.getPayload() + " - subscriber-1"} -->
         handle.using{m:Message[_] => println(m)}
         ,
         transform.using{m:Message[_] => m.getPayload() + " - subscriber-2"} -->
         handle.using{m:Message[_] => println(m)}
      )
      
    messageFlow.send("hello")
    println("done")
  }
  
  
  
  @Test
  def simpleCompositionTestSendWithBridge = {
    val messageFlow = 
      Channel("A") -->
      Channel("B") -->
      handle.using{m:Message[_] => println("From Hello channel - " + m)}
      
    messageFlow.send("hello")
    
    println("done")
  }
  
//  @Test
//  def simpleCompositionTestSendWithPolingBridge = {
//    val messageFlow = 
//      Channel("A") -->
//      Channel("B").withQueue --> poll.usingFixedRate(1) -->
//      handle.using{m:Message[_] => println("From Hello channel - " + m)}
//      
//    messageFlow.send("hello")
//    
//    println("done")
//  }
  
  def simpleCompositionWithEnricher = {
    val enrichFlow = 
      handle.using("someSpel") -->
      transform.using("someSpel")
      
    val bazEnrichFlow = 
      handle.using("someSpel") -->
      transform.using("someSpel")
      
//    val messageFlow = 
//      enrich.header.using("hello" -> "bye", "foo" -> "@myBean.foo()", "baz" -> {m:Message[_] => bazEnrichFlow.sendAndReceive(m)}) -->
//      enrich.payload.withData{m:Message[_] => m} -->
//      handle.using{m:Message[_] => println(m);m} -->
//      enrich.payload.using{payload:String => enrichFlow.sendAndReceive(payload)} -->
//      handle.using{m:Message[_] => println(m);m}
  }
}