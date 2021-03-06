import java.util.*;

/**
 * Created by Nitish on 10/13/2016.
 */
public class AssociationRuleFinder {

    public Map<List<String>, Integer> combinationMap;
    public List<List<String>> RuleList;
    public List<List<String>> BodyList;
    public List<List<String>> HeadList;

    double confidenceval = 0;
    double support = 0;

    public AssociationRuleFinder(double support, double confidence) throws Exception {
        this.support = support;
        this.confidenceval = confidence;
        FrequentItemSetFinder finder = new FrequentItemSetFinder();
        FrequentItemSetFinder freqItemFinder = new FrequentItemSetFinder();
        List<List<String>> transItemList = freqItemFinder.readGeneDataSet("gene_expression.csv");
        this.combinationMap = finder.frequentItemSetGenerator(support, transItemList);
        this.RuleList = new ArrayList<List<String>>();
        this.BodyList = new ArrayList<List<String>>();
        this.HeadList = new ArrayList<List<String>>();

    }

    public void getAllPossibleAssociationRules() {

        Iterator<Map.Entry<List<String>, Integer>> iterator = combinationMap.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<List<String>, Integer> entry = iterator.next();
            List<String> key = entry.getKey();
            Integer keysup = entry.getValue();

            List<List<String>> generatedsubsetlist = generateSubset(key);
            List<List<String>> generatedsubsetlistcopy = new ArrayList<List<String>>(generatedsubsetlist);
            //List<List<String>> generatedsubsetlistcopy = generateSubset(key);

            Iterator<List<String>> listit = generatedsubsetlist.iterator();

            while (listit.hasNext()) {
                List<String> itemlist = listit.next();

                if (itemlist.size() != 0 && itemlist.size() != key.size()) {
                    Iterator<List<String>> listitcopy = generatedsubsetlistcopy.iterator();

                    while (listitcopy.hasNext()) {
                        List<String> itemlistcopy = listitcopy.next();
                        if (itemlistcopy.size() != 0 && itemlistcopy.size() != key.size()) {
                            if (checkRuleValid(itemlist, itemlistcopy)) {

                                List<String> rule = new ArrayList<String>();
                                rule.addAll(itemlist);
                                rule.add("->");
                                rule.addAll(itemlistcopy);

                                if (!RuleList.contains(rule)) {
                                    RuleList.add(rule);
                                    BodyList.add(itemlist);
                                    HeadList.add(itemlistcopy);
                                }
                            }

                        }
                    }


                }
            }
        }
        System.out.println("The list of generated rules are ");
        System.out.println(RuleList);
        System.out.println("Number of Rules Generated = " + RuleList.size());
        //System.out.println(BodyList);
        //System.out.println(HeadList);

        List<String> qstring1 = new ArrayList<String>();
        qstring1.add("G6_UP");
        samplequerytemplate1("RULE", "ANY", qstring1, "Template1-Query1");

        List<String> qstring2 = new ArrayList<String>();
        qstring2.add("G1_UP");
        samplequerytemplate1("RULE", "1", qstring2, "Template1-Query2");

        List<String> qstring3 = new ArrayList<String>();
        qstring3.add("G1_UP");
        qstring3.add("G10_Down");
        samplequerytemplate1("RULE", "1", qstring3, "Template1-Query3");

        List<String> qstring4 = new ArrayList<String>();
        qstring4.add("G6_UP");
        samplequerytemplate1("BODY", "ANY", qstring4, "Template1-Query4");

        List<String> qstring5 = new ArrayList<String>();
        qstring5.add("G72_UP");
        samplequerytemplate1("BODY", "NONE", qstring5, "Template1-Query5");

        List<String> qstring6 = new ArrayList<String>();
        qstring6.add("G1_UP");
        qstring6.add("G10_Down");
        samplequerytemplate1("BODY", "1", qstring6, "Template1-Query6");

        List<String> qstring7 = new ArrayList<String>();
        qstring7.add("G6_UP");
        samplequerytemplate1("HEAD", "ANY", qstring7, "Template1-Query7");

        List<String> qstring8 = new ArrayList<String>();
        qstring8.add("G1_UP");
        qstring8.add("G6_UP");
        samplequerytemplate1("HEAD", "NONE", qstring8, "Template1-Query8");

        List<String> qstring9 = new ArrayList<String>();
        qstring9.add("G6_UP");
        qstring9.add("G8_UP");
        samplequerytemplate1("HEAD", "1", qstring9, "Template1-Query9");

        List<String> qstring10 = new ArrayList<String>();
        qstring10.add("G1_UP");
        qstring10.add("G6_UP");
        qstring10.add("G72_UP");
        samplequerytemplate1("RULE", "1", qstring10, "Template1-Query10");

        List<String> qstring11 = new ArrayList<String>();
        qstring11.add("G1_UP");
        qstring11.add("G6_UP");
        qstring11.add("G72_UP");
        samplequerytemplate1("RULE", "ANY", qstring11, "Template1-Query11");

        samplequerytemplate2("RULE", 3, "Template2-Query1");
        samplequerytemplate2("BODY", 2, "Template2-Query2");
        samplequerytemplate2("HEAD", 2, "Template2-Query3");

        samplequerytemplate3_1();
        samplequerytemplate3_2();
        samplequerytemplate3_3();
        samplequerytemplate3_4();
        samplequerytemplate3_5();
        samplequerytemplate3_6();

    }


    public double findconfidence(List<String> lhsstring, List<String> rhsstring) {
        int lhssup = combinationMap.get(lhsstring);
        int rhssup = combinationMap.get(rhsstring);

        List<String> tempunionlist = new ArrayList<String>(lhsstring);
        tempunionlist.addAll(rhsstring);

        Collections.sort(tempunionlist);

        int unionsup = combinationMap.get(tempunionlist);

        double confval = (double) unionsup / lhssup;

        return confval;

    }


    public List<List<String>> generateSubset(List<String> powerset) {
        List<List<String>> subsets = new ArrayList<List<String>>();
        if (powerset.isEmpty()) {
            subsets.add(new ArrayList<String>());
            return subsets;
        }

        String head = powerset.get(0);
        List<String> rest = new ArrayList<String>(powerset.subList(1, powerset.size()));
        for (List<String> sublist : generateSubset(rest)) {
            List<String> newlist = new ArrayList<String>();
            newlist.add(head);
            newlist.addAll(sublist);
            subsets.add(newlist);
            subsets.add(sublist);
        }

        return subsets;

    }

    public boolean checkRuleValid(List<String> checklhs, List<String> checkrhs) {
        int flag = 0;

        for (String lhsitem : checklhs) {
            if (checkrhs.contains(lhsitem)) {
                flag = 1;
                break;
            }
        }

        if (flag == 0) {
            double confvalue = findconfidence(checklhs, checkrhs);
            if (confvalue >= confidenceval) {
                return true;
            }
        }

        return false;
    }


    public void samplequery1() {
        int count = 0;

        for (int i = 0; i < RuleList.size(); i++) {
            if (RuleList.get(i).contains("G1_UP") || RuleList.get(i).contains("G10_Down")) {
                count = count + 1;
                continue;
            }
        }

        System.out.println("Number of rules for query 1 = " + count);

    }

    public void samplequery2() {
        int count = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            if (BodyList.get(i).contains("G6_UP")) {
                count = count + 1;
                continue;
            }
        }

        System.out.println("Number of rules for query 1 = " + count);

    }

    public void samplequerytemplate1(String part1, String clause, List<String> genes, String query) {
        int count = 0;

        if (part1.equals("RULE")) {
            if (clause.equals("ANY")) {
                for (int i = 0; i < RuleList.size(); i++) {
                    for (int j = 0; j < genes.size(); j++) {
                        if (RuleList.get(i).contains(genes.get(j))) {
                            count = count + 1;
                            break;
                        }
                    }
                }
            } else if (clause.equals("NONE")) {
                for (int i = 0; i < RuleList.size(); i++) {
                    int flag = 0;
                    for (int j = 0; j < genes.size(); j++) {
                        if (RuleList.get(i).contains(genes.get(j))) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        count = count + 1;
                    }
                }
            } else if (clause.equals("1")) {

                for (int i = 0; i < RuleList.size(); i++) {
                    int flag = 0;
                    for (int j = 0; j < genes.size(); j++) {
                        if (RuleList.get(i).contains(genes.get(j))) {
                            flag = flag + 1;
                        }
                    }
                    if (flag == 1) {
                        count = count + 1;
                    }
                }
            }
        } else if (part1.equals("BODY")) {
            if (clause.equals("ANY")) {
                for (int i = 0; i < BodyList.size(); i++) {
                    for (int j = 0; j < genes.size(); j++) {
                        if (BodyList.get(i).contains(genes.get(j))) {
                            count = count + 1;
                            break;
                        }
                    }
                }
            } else if (clause.equals("NONE")) {
                for (int i = 0; i < BodyList.size(); i++) {
                    int flag = 0;
                    for (int j = 0; j < genes.size(); j++) {
                        if (BodyList.get(i).contains(genes.get(j))) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        count = count + 1;
                    }
                }
            } else if (clause.equals("1")) {
                for (int i = 0; i < BodyList.size(); i++) {
                    int flag = 0;
                    for (int j = 0; j < genes.size(); j++) {
                        if (BodyList.get(i).contains(genes.get(j))) {
                            flag = flag + 1;
                        }
                    }
                    if (flag == 1) {
                        count = count + 1;
                    }
                }
            }

        } else if (part1.equals("HEAD")) {
            if (clause.equals("ANY")) {
                for (int i = 0; i < HeadList.size(); i++) {
                    for (int j = 0; j < genes.size(); j++) {
                        if (HeadList.get(i).contains(genes.get(j))) {
                            count = count + 1;
                            break;
                        }
                    }
                }
            } else if (clause.equals("NONE")) {
                for (int i = 0; i < HeadList.size(); i++) {
                    int flag = 0;
                    for (int j = 0; j < genes.size(); j++) {
                        if (HeadList.get(i).contains(genes.get(j))) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        count = count + 1;
                    }
                }
            } else if (clause.equals("1")) {
                for (int i = 0; i < HeadList.size(); i++) {
                    int flag = 0;
                    for (int j = 0; j < genes.size(); j++) {
                        if (HeadList.get(i).contains(genes.get(j))) {
                            flag = flag + 1;
                        }
                    }
                    if (flag == 1) {
                        count = count + 1;
                    }
                }
            }
        }

        System.out.println(query + " Number of generated rules = " + count);

    }

    public void samplequerytemplate2(String part1, int counter, String query) {
        int count = 0;
        int checker = 0;

        if (part1.equals("RULE")) {
            for (int i = 0; i < RuleList.size(); i++) {
                checker = RuleList.get(i).size();
                if (checker - 1 >= counter) {
                    count = count + 1;
                    continue;
                }
            }
        } else if (part1.equals("BODY")) {
            for (int i = 0; i < BodyList.size(); i++) {
                checker = BodyList.get(i).size();
                if (checker >= counter) {
                    count = count + 1;
                }
            }
        } else if (part1.equals("HEAD")) {
            for (int i = 0; i < HeadList.size(); i++) {
                checker = HeadList.get(i).size();
                if (checker >= counter) {
                    count = count + 1;
                }
            }
        }

        System.out.println(query + " Number of generated rules = " + count);
    }

    public void samplequerytemplate3_1() {
        int count = 0;
        int flag1 = 0;
        //int flag2 = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            if (BodyList.get(i).contains("G1_UP") && HeadList.get(i).contains("G59_UP")) {
                count = count + 1;
                break;
            }
        }

        System.out.println("Template3-Query1 Number of generated rules = " + count);
    }

    public void samplequerytemplate3_2() {
        int count = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            int flag1 = 0;
            if (BodyList.get(i).contains("G1_UP")) {
                flag1 = flag1 + 1;
            }
            if (HeadList.get(i).contains("G6_UP")) {
                flag1 = flag1 + 1;
            }

            if (flag1 == 1 || flag1 == 2) {
                count = count + 1;
            }
        }

        System.out.println("Template3-Query2 Number of generated rules = " + count);
    }

    public void samplequerytemplate3_3() {
        int count = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            int flag = 0;
            if (BodyList.get(i).contains("G1_UP")) {
                flag = flag + 1;
            }
            if (Collections.frequency(HeadList.get(i), "G6_UP") == 2) {
                flag = flag + 1;
            }

            if (flag == 1 || flag == 2) {
                count = count + 1;
            }
        }


        System.out.println("Template3-Query3 Number of generated rules = " + count);
    }

    public void samplequerytemplate3_4() {
        int count = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            if (HeadList.get(i).contains("G1_UP") && ((!BodyList.get(i).contains("AML")) || (!BodyList.get(i).contains("ALL")) || (!BodyList.get(i).contains("Breast Cancer")) || (!BodyList.get(i).contains("Colon Cancer")))) {
                count = count + 1;
            }
        }

        System.out.println("Template3-Query4 Number of generated rules = " + count);
    }

    public void samplequerytemplate3_5() {
        int count = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            int flag1 = 0;
            int flag2 = 0;
            if (HeadList.get(i).contains("AML")) {
                flag1 = flag1 + 1;
            }
            if (HeadList.get(i).contains("ALL")) {
                flag1 = flag1 + 1;
            }
            if (HeadList.get(i).contains("Breast Cancer")) {
                flag1 = flag1 + 1;
            }
            if (HeadList.get(i).contains("Colon Cancer")) {
                flag1 = flag1 + 1;
            }
            if (RuleList.get(i).contains("G72_UP")) {
                flag2 = flag2 + 1;
            }
            if (RuleList.get(i).contains("G96_Down")) {
                flag2 = flag2 + 1;
            }

            if (flag1 == 1 || flag2 == 1) {
                count = count + 1;
            }

        }

        System.out.println("Template3-Query5 Number of generated rules = " + count);
    }

    public void samplequerytemplate3_6() {
        int count = 0;

        for (int i = 0; i < BodyList.size(); i++) {
            int flag1 = 0;
            int flag2 = 0;
            if (BodyList.get(i).contains("G59_UP")) {
                flag1 = flag1 + 1;
            }
            if (BodyList.get(i).contains("G96_Down")) {
                flag1 = flag1 + 1;
            }
            if ((RuleList.get(i).size() - 1) >= 3) {
                flag2 = flag2 + 1;
            }

            if (flag1 == 1 && flag2 == 1) {
                count = count + 1;
            }
        }

        System.out.println("Template3-Query6 Number of generated rules = " + count);
    }
}
