package my.app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        String directory = "src/test/resources";
        List<File> files = listFiles(directory);

        for (File f : files) {
            String txt = Files.readString(Path.of(f.toString()));

            Node document = parser.parse(txt);
            document.accept(visitor);

            Map<String, List<String>> data = visitor.getData();
            assertTrue(data.containsKey("literal"));

            //HtmlRenderer renderer = HtmlRenderer.builder().build();
            //String res = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
            //System.err.println(res);
        }
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
