package my.app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testng.AssertJUnit.*;


import java.io.*;
import java.util.*;
import java.nio.file.*;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.parser.block.*;

import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.*; //YamlFrontMatterExtension;

public class AppTest 
{
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void processDocs() throws Exception
    {
        List<Extension> extensions = Arrays.asList(YamlFrontMatterExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();

        assertTrue( true );
//        String directory = "src/test/resources";
        String directory = "site/content/english/blog";
        List<File> files = listFiles(directory);

        for (File f : files) {
            String txt = Files.readString(Path.of(f.toString()));

            Node document = parser.parse(txt);
            document.accept(visitor);

            Map<String, List<String>> frontMatter = visitor.getData();

            //TODO enhanceFrontMatterWithGoogleAnalytics()
            //TODO enhanceFrontMatterWithAhrefs()
            validateFrontMatter(frontMatter, f.toString());
            

            //HtmlRenderer renderer = HtmlRenderer.builder().build();
            //String res = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
            //System.err.println(res);
        }
    }

    private static void validateFrontMatter(final Map<String, List<String>> frontMatter, String filename) {
        assert frontMatter.containsKey("type") : filename + " has no type";
        List<String> types = frontMatter.get("type");
        assert types.size() == 1 : filename + " expected a single type";
        String type = types.get(0);
        assert type.compareTo("post") == 0 : filename + " expected type 'post', but got '" + type + "'";

        assert frontMatter.containsKey("author") : filename + " has no author";
        List<String> authors = frontMatter.get("author");
        assert authors.size() == 1 : filename + " expected a single author";
        String author = authors.get(0);
        assert author.length() > 0 : filename + " expected a non-zero character author";

        assert frontMatter.containsKey("title") : filename + " has no title";
        List<String> titles = frontMatter.get("title");
        assert titles.size() == 1 : filename + " expected a single title";
        String title = titles.get(0);
        assert title.length() >= 50 && title.length() <= 70 : filename + " expected a 50-70 character title";

        assert frontMatter.containsKey("meta_title") : filename + " has no meta_title";
        List<String> metatitles = frontMatter.get("meta_title");
        assert metatitles.size() == 1 : filename + " expected a single meta_title";
        String metatitle = metatitles.get(0);
        assert metatitle.length() >= 150 && metatitle.length() <= 160 : filename + " expected a 150-160 character meta_title";
    }


    private static List<File> listFiles(final String directory) {
        if (directory == null) {
            return Collections.EMPTY_LIST;
        }
        List<File> fileList = new ArrayList<>();
        File[] files = new File(directory).listFiles();
        for (File element : files) {
            if (element.isDirectory()) {
                fileList.addAll(listFiles(element.getPath()));
            } else {
                fileList.add(element);
            }
        }
        return fileList;
    }
}
