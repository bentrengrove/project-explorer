package com.bentrengrove.compose_markdown

import android.util.Log
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
    val root = parser.parse("#Hello\n\nThis is some text") as Document
    Markdown(root)
}

@Composable
fun Markdown(document: Document) {
    MarkdownChild(document)
}

@Composable
fun MarkdownChild(parent: Node) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is Heading -> MarkdownHeading(child)
            is Paragraph -> MarkdownParagraph(child)
            else -> { Log.w(TAG, "${child::class.java} not currently supported") }
        }

        child = child.next
    }
}

@Composable
fun MarkdownHeading(heading: Heading) {
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
fun MarkdownParagraph(paragraph: Paragraph) {
    val styledText = buildAnnotatedString {
        pushStyle(MaterialTheme.typography.body1.toSpanStyle())
        appendMarkdownChildren(paragraph, MaterialTheme.colors)
        pop()
    }
    MarkdownText(styledText, MaterialTheme.typography.body1)
}

@Composable
fun MarkdownText(text: AnnotatedString, style: TextStyle, modifier: Modifier = Modifier) {
    Text(text = text, style = style, modifier = modifier)
}

fun AnnotatedString.Builder.appendMarkdownChildren(
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
            is HardLineBreak -> {
                append("\n")
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