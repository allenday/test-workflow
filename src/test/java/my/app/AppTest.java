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

        Map<String,Map<String,Boolean>> res = new HashMap<String,Map<String,Boolean>>();
        for (File f : files) {
            String txt = Files.readString(Path.of(f.toString()));

            Node document = parser.parse(txt);
            document.accept(visitor);

            Map<String, List<String>> frontMatter = visitor.getData();

            //TODO enhanceFrontMatterWithGoogleAnalytics()
            //TODO enhanceFrontMatterWithAhrefs()

            Map<String,Boolean> r = validateFrontMatter(frontMatter);
            res.put(f.toString(), r);

            //HtmlRenderer renderer = HtmlRenderer.builder().build();
            //String res = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
            //System.err.println(res);
        }

        for (String k : res.keySet()) {
          System.err.println(k);
          Map<String,Boolean> h = res.get(k);
          for (String r : h.keySet()) {
            System.err.println("\t"+r+"\t"+h.get(r));
          }
        }
    }

    private static Map<String,Boolean> validateFrontMatter(final Map<String, List<String>> frontMatter) throws IOException {
        Set<String> catWhitelist = new HashSet<String>();
        Set<String> tagWhitelist = new HashSet<String>();
        String[] cat_ = Files.readString(Path.of("site/content/english/meta/category.txt")).split("\n");
        String[] tag_ = Files.readString(Path.of("site/content/english/meta/tag.txt")).split("\n");
        for (String x : cat_) { catWhitelist.add(x); }
        for (String x : tag_) { tagWhitelist.add(x); }

        Map<String,Boolean> res = new HashMap<String,Boolean>();


        res.put("frontmatter.type", false);
        if (frontMatter.containsKey("type") && frontMatter.get("type").size() == 1) {
          String type = frontMatter.get("type").get(0);
          res.put("frontmatter.type", type.compareTo("post") == 0);
        }

        res.put("frontmatter.author", false);
        if (frontMatter.containsKey("author") && frontMatter.get("author").size() == 1) {
          String author = frontMatter.get("author").get(0);
          res.put("frontmatter.author", author.length() > 0);
        }

        res.put("frontmatter.title", false);
        if (frontMatter.containsKey("title") && frontMatter.get("title").size() == 1) {
          String title = frontMatter.get("title").get(0);
          res.put("frontmatter.title", title.length() >= 50 && title.length() <= 60);
        }

        res.put("frontmatter.meta_title", false);
        if (frontMatter.containsKey("meta_title") && frontMatter.get("meta_title").size() == 1) {
          String meta = frontMatter.get("meta_title").get(0);
          res.put("frontmatter.meta_title", meta.length() >= 50 && meta.length() <= 60);
        }

        res.put("frontmatter.description", false);
        if (frontMatter.containsKey("description") && frontMatter.get("description").size() == 1) {
          String meta = frontMatter.get("description").get(0);
          res.put("frontmatter.description", meta.length() >= 150 && meta.length() <= 160);
        }

        res.put("frontmatter.keywords", false);
        if (frontMatter.containsKey("keywords") && frontMatter.get("keywords").size() == 1) {
          String meta = frontMatter.get("keywords").get(0);
          res.put("frontmatter.keywords", meta.split(",").length >= 3 && meta.split(",").length <= 10);
        }

        res.put("frontmatter.image", false);
        if (frontMatter.containsKey("image") && frontMatter.get("image").size() == 1) {
          String meta = frontMatter.get("image").get(0);
          res.put("frontmatter.image", meta.length() > 0);
        }

        res.put("frontmatter.date", false);
        if (frontMatter.containsKey("date") && frontMatter.get("date").size() == 1) {
          String date = frontMatter.get("date").get(0);
          res.put("frontmatter.date", date.length() > 0);
        }

        res.put("frontmatter.bg_image", false);
        if (frontMatter.containsKey("bg_image") && frontMatter.get("bg_image").size() == 1) {
          String img = frontMatter.get("bg_image").get(0);
          res.put("frontmatter.bg_image", img.length() > 0);
        }

        res.put("frontmatter.blog_category", false);
        if (frontMatter.containsKey("blog_category") && frontMatter.get("blog_category").size() == 1) {
          String cat = frontMatter.get("blog_category").get(0);
          res.put("frontmatter.blog_category", cat.length() > 0);
        }

        res.put("frontmatter.tags", false);
        if (frontMatter.containsKey("tags")) {
          List<String> tags = frontMatter.get("tags");
          Boolean stat = true;
          for (String tag : tags) {
            if (! tagWhitelist.contains(tag)) {
              stat = false;
            }
          }
          res.put("frontmatter.tags", stat);
        }

        res.put("frontmatter.category", false);
        if (frontMatter.containsKey("category")) {
          List<String> cats = frontMatter.get("category");
          Boolean stat = true;
          for (String cat : cats) {
            if (! catWhitelist.contains(cat)) {
              stat = false;
            }
          }
          res.put("frontmatter.category", stat);
        }

        return res;
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
