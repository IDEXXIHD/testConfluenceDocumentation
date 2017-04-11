import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DocletWriter {
    private static final String FILENAME = "DocumentationOutput.html";
    private static boolean outputmethods=false;
    public static boolean start(RootDoc root) {
        System.out.println("Start.");
        ArrayList<String> alltags=new ArrayList<String>();
        String metaTag="meta";alltags.add(metaTag);
        String dateTag="date";alltags.add(dateTag);
        String authorTag="author";alltags.add(authorTag);
        String purposeTag="purpose";alltags.add(purposeTag);
        String descriptionTag="description";alltags.add(descriptionTag);
        String issueTag="issue";alltags.add(issueTag);
        String preconditionTag="precondition";alltags.add(preconditionTag);
        String assertTag="assert";alltags.add(assertTag);
        String cleanupTag="cleanup";alltags.add(cleanupTag);
        writeContents(root.classes(), alltags);
        return true;
    }
    private static ArrayList<ObjectByDateContainer> sortUsingContainer(ClassDoc[] classes){
        ArrayList<ObjectByDateContainer> thelist=new ArrayList<ObjectByDateContainer>();
        for (int i=0; i < classes.length; i++) {
            ObjectByDateContainer odc=new ObjectByDateContainer(getDateFromClass(classes[i]),classes[i] );
            thelist.add(odc);
        }
        //Sort it
        Collections.sort(thelist);
        return thelist;
    }
    private static void writeContents(ClassDoc[] classes, ArrayList<String> alltags) {
        StringBuffer content=new StringBuffer();
        content.append("<html>\n");
        content.append("<H1>IVLS Integration Test Documentation</H1>\n");
        ArrayList<ObjectByDateContainer> al=sortUsingContainer(classes);
        for(int i=0;i<al.size();i++)
        {
            content.append("<table border='1'>\n");
            content.append("<tr><td bgcolor=\"#99ccff\">Class:</td><td> " + al.get(i).getClassDoc().toString()+ "</td></tr>\n");
            ClassDoc obd=al.get(i).getClassDoc();
            Tag[] classtag=obd.tags();
            for(int u=0;u<classtag.length;u++ ){
                content.append("<tr><td>" + classtag[u].name() + ": </td><td>" + classtag[u].text() + "</td></tr>\n");
            }
            content.append("</table>\n");
            if(outputmethods) {
                outputMethodData(i, classes, content, alltags);
            }

        }
        content.append("</html>\n");
        writeOutput(content);
    }

    public static void outputMethodData(int currentclass, ClassDoc[] classes,StringBuffer content,ArrayList<String> alltags)
    {
            content.append("<table border='1'>\n");
            MethodDoc[] methods = classes[currentclass].methods();
            for (int j=0; j < methods.length; j++) {
                content.append("<tr><td bgcolor=\"#70db70\">" + "Method:</td><td>" + methods[j].name()+ "</td></tr>\n");
                for(String t:alltags) {
                    Tag[] tags = methods[j].tags(t);
                    if (tags.length > 0)
                    {
                       for (int k = 0; k < tags.length; k++)
                        {
                            content.append("<tr><td>" + tags[k].name() + ":</td><td>"
                                    + tags[k].text() + "</td></tr>\n");
                        }
                    }
                }
                content.append("<tr><td></td></tr>\n");
            }
            content.append("</table>\n");
    }
    public static void writeOutput(StringBuffer content) {

        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(FILENAME);
            bw = new BufferedWriter(fw);
            bw.write(content.toString());
            System.out.println("Done.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static Date getDateFromClass(ClassDoc classy){
        Tag[] classtag2 = classy.tags();
        Date d=null;
        for (int u = 0; u < classtag2.length; u++) {
            if (classtag2[u].name().contains("date")) {
                d=getDateStraightFromTag(classtag2[u]);
            }
        }
       return d;
    }
    private static Date getDateStraightFromTag(Tag t){
        Date dd = new Date();
        try {
            String sdate = "" + t;
            //need to convert to date so can get long value for the sort
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            //Note: using '6' for substring is weird but the date comes from the DocClass in a fixed format: @date:M/DD/YYYY
            //This is independent of how the @date is actually formatted, so long as it's recognized
            String jd = sdate.substring(6);
            dd = sdf.parse(jd);

        }catch (Exception ex){ex.printStackTrace();}
        return dd;
    }

    private static class ObjectByDateContainer implements Comparable<ObjectByDateContainer> {

        private Date dateTime;
        ClassDoc orig=null;
        public ClassDoc getClassDoc(){return orig;}
        public ObjectByDateContainer(Date date,ClassDoc original ){
            this.dateTime=date;
            orig=original;
        }
        public Date getDateTime() {
            return dateTime;
        }

        public void setDateTime(Date datetime) {
            this.dateTime = datetime;
        }

        @Override
        public int compareTo(ObjectByDateContainer o) {
                if(getDateTime().getTime()<o.getDateTime().getTime()) {return 1;}
                else {return -1;}
        }
    }
}
