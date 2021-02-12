package com.bentrengrove.compose_markdown

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import org.commonmark.node.*
import org.commonmark.parser.Parser

private const val TAG = "compose_markdown"
private const val TAG_URL = "url"
private const val TAG_IMAGE_URL = "imageUrl"

@Preview
@Composable
fun MarkdownPreview() {
    val parser = Parser.builder().build()
    val root = parser.parse("#### Heading\nThis is some *italic* and **bold** text") as Document
    Markdown(root)
}

@Composable
fun Markdown(document: Document, modifier: Modifier = Modifier) {
    Column(modifier) {
        MarkdownChild(document)
    }
}

@Composable
private fun MarkdownChild(parent: Node) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is Heading -> MarkdownHeading(child)
            is Paragraph -> MarkdownParagraph(child)
            is BulletList -> MarkdownBulletList(bulletList = child)
            is OrderedList -> MarkdownOrderedList(orderedList = child)
            else -> { Log.w(TAG, "${child::class.java} not currently supported") }
        }

        child = child.next
    }
}

@Composable
private fun MarkdownHeading(heading: Heading) {
    val style = when (heading.level) {
        1 -> MaterialTheme.typography.h1
        2 -> MaterialTheme.typography.h2
        3 -> MaterialTheme.typography.h3
        4 -> MaterialTheme.typography.h4
        5 -> MaterialTheme.typography.h5
        else -> MaterialTheme.typography.h6
    }

    val text = buildAnnotatedString {
        appendMarkdownChildren(heading, MaterialTheme.colors)
    }
    MarkdownText(text, style)
}

@Composable
private fun MarkdownParagraph(paragraph: Paragraph) {
    val styledText = buildAnnotatedString {
        pushStyle(MaterialTheme.typography.body1.toSpanStyle())
        appendMarkdownChildren(paragraph, MaterialTheme.colors)
        pop()
    }

    MarkdownText(styledText, MaterialTheme.typography.body1)
}

@Composable
private fun MarkdownText(text: AnnotatedString, style: TextStyle, modifier: Modifier = Modifier) {
    Text(text = text, style = style, modifier = modifier)
}

@Composable
private fun MarkdownBulletList(bulletList: BulletList, modifier: Modifier = Modifier) {
    val marker = bulletList.bulletMarker
    MarkdownListItems(bulletList, modifier = modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("$marker ")
            appendMarkdownChildren(it, MaterialTheme.colors)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}

@Composable
fun MarkdownOrderedList(orderedList: OrderedList, modifier: Modifier = Modifier) {
    var number = orderedList.startNumber
    val delimiter = orderedList.delimiter
    MarkdownListItems(orderedList, modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("${number++}$delimiter ")
            appendMarkdownChildren(it, MaterialTheme.colors)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}

@Composable
private fun MarkdownListItems(listBlock: ListBlock, modifier: Modifier = Modifier, item: @Composable (node: Node) -> Unit) {
    Column {
        var listItem = listBlock.firstChild
        while (listItem != null) {
            var child = listItem.firstChild
            while (child != null) {
                when (child) {
                    is BulletList -> MarkdownBulletList(child, modifier)
                    is OrderedList -> MarkdownOrderedList(child, modifier)
                    else -> item(child)
                }
                child = child.next
            }
            listItem = listItem.next
        }
    }
}

private fun AnnotatedString.Builder.appendMarkdownChildren(
    parent: Node, colors: Colors
) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is Paragraph -> appendMarkdownChildren(child, colors)
            is Text -> append(child.literal)
            is Image -> appendInlineContent(TAG_IMAGE_URL, child.destination)
            is Emphasis -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is StrongEmphasis -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is Code -> {
                pushStyle(TextStyle(fontFamily = FontFamily.Monospace).toSpanStyle())
                append(child.literal)
                pop()
            }
            is SoftLineBreak -> {
                append("\n")
            }
            is HardLineBreak -> {
                append("\n\n")
            }
            is Link -> {
                val underline = SpanStyle(colors.primary, textDecoration = TextDecoration.Underline)
                pushStyle(underline)
                pushStringAnnotation(TAG_URL, child.destination)
                appendMarkdownChildren(child, colors)
                pop()
                pop()
            }
        }
        child = child.next
    }
}