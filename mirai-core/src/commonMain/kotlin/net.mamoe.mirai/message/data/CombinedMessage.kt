/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.message.data

/**
 * Left-biased list
 */
class CombinedMessage(
    val left: Message,
    val element: Message
) : Iterable<Message>, Message {
    private suspend fun SequenceScope<Message>.yieldCombinedOrElements(message: Message) {
        when (message) {
            is CombinedMessage -> {
                yieldCombinedOrElements(message.element)
                yieldCombinedOrElements(message.left)
            }
            is MessageChain -> message.forEach { yieldCombinedOrElements(it) }
            else -> yield(message)
        }
    }

    fun asSequence(): Sequence<Message> = sequence {
        yieldCombinedOrElements(this@CombinedMessage)
    }

    override fun iterator(): Iterator<Message> {
        return asSequence().iterator()
    }

    override fun toString(): String {
        return left.toString() + element.toString()
    }
}