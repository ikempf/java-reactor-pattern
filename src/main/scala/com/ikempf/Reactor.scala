package com.ikempf

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel}
import java.nio.charset.StandardCharsets

import com.ikempf.Comp.{attachInputChannels, startListenLoop}

/**
  * socket ---> Channel1 --->          ---> Handler1
  * socket ---> Channel2 ---> Selector ---> Handler2
  * socket ---> Channel3 --->          ---> Handler3
  */
object Reactor extends App {

  val selector = Selector.open()
  attachInputChannels(selector)
  startListenLoop(selector)

}

object Comp {

  def attachInputChannels(selector: Selector): Unit =
    (10050 until 10100).map(port => {
      val channel = ServerSocketChannel.open()
      channel.socket().bind(new InetSocketAddress(port))

      channel
        .configureBlocking(false)
        .register(selector, SelectionKey.OP_ACCEPT)
        .attach(Handler(channel))
    })

  case class Handler(channel: ServerSocketChannel) {

    def handle(): Unit = {
      val buffer    = ByteBuffer.allocate(1000)
      val remaining = channel.accept().read(buffer)

      println(s"Read $remaining bytes")
      println(new String(buffer.array(), StandardCharsets.UTF_8))
    }
  }

  def startListenLoop(selector: Selector): Unit =
    while (true) {
      selector.select()
      val selectedKeys = selector.selectedKeys().iterator()

      while (selectedKeys.hasNext) {
        val key     = selectedKeys.next()
        val handler = key.attachment().asInstanceOf[Handler]
        handler.handle()
        selectedKeys.remove()
      }
    }

}
