package io.github.itamarc.tmplpages;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.emoji.internal.EmojiReference;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import java.util.Arrays;

public class MarkdownProcessor {
    /**
     * Convert Markdown to HTML using com.vladsch.flexmark
     * 
     * @param filled The snippet or template already filled as Markdown
     * @return The received content converted to HTML
     */
    public String processMarkdown(String filled) {
        EmojiExtension emojiExt = EmojiExtension.create();
        MutableDataSet options = new MutableDataSet();
        options.set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.GITHUB);
        options.set(EmojiExtension.ROOT_IMAGE_PATH, EmojiReference.githubUrl);
        emojiExt.parserOptions(options);
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create(), emojiExt));

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(filled);

        String html = renderer.render(document);

        return html;
    }
}
