import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Example {
    public static void main(String[] args) throws IOException {
        // InputStream in = new FileInputStream("payroll.sp");
        InputStream in = new FileInputStream("/Users/uengine/Documents/payroll/sp/pg_pay_pay.pck");
        CharStream s = CharStreams.fromStream(in);

        CaseChangingCharStream upper = new CaseChangingCharStream(s, true);

        PlSqlLexer lexer = new PlSqlLexer(upper);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // PlSqlParser parser = new PlSqlParser(tokens);

        // Object sql_scriptContext = parser.sql_script();

        PlSqlParser parser = new PlSqlParser(tokens);
        ParserRuleContext tree = parser.sql_script(); // 구문 분석 트리의 루트를 얻습니다.

        ParseTreeWalker walker = new ParseTreeWalker();
        CustomPlSqlListener listener = new CustomPlSqlListener(tokens);
        walker.walk(listener, tree); // 리스너를 구문 분석 트리에 연결합니다.

        listener.printStructure();

        System.out.println(listener.getRoot().toJson());
        try (FileWriter file = new FileWriter("structure.json")) {
            file.write(listener.getRoot().toJson());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
