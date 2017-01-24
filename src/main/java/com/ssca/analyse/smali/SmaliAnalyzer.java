/**
 * 
 */
package com.ssca.analyse.smali;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.tree.CommonTree;
import org.apache.logging.log4j.*;
import org.jf.smali.LexerErrorInterface;
import org.jf.smali.smaliFlexLexer;
import org.jf.smali.smaliParser;

import com.ssca.Main.Config;
import com.ssca.analyse.smali.bound.ActivityInBound;
import com.ssca.analyse.smali.bound.IntentData;
import com.ssca.analyse.smali.bound.RecieverInBound;
import com.ssca.analyse.smali.bound.ServiceInBound;
import com.ssca.analyse.smali.controlflow.GetControlFlow;
import com.ssca.commonData.CommonData;

/**
 * @author yujianbo
 *
 * 2016年1月25日
 */
public class SmaliAnalyzer {

	private static List<File> smaliFileList = new ArrayList();
	private static String fileSeparator = System.getProperty("file.separator");
	private static Logger logger = LogManager.getLogger(SmaliAnalyzer.class);
	
	public void doSmaliAnalyse(){
		
		getSmaliFiles(CommonData.getDecompilePath());
		
		logger.info("smali start!");
		for(File smaliFile:smaliFileList){
			logger.info("start analyse "+smaliFile.getAbsolutePath());
			try {
				//解析每个smali文件，获取语法树
				CommonTree t = getCommonTree(smaliFile, false, CommonData.getWhiteList());
				//根据smali语法树，获取控制流
				GetControlFlow.GetFlow(t, smaliFile.getName(), CommonData.getWhiteList());
				t=null;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		logger.info("smali end!");
		
		logger.info("start get activity bound!");
		//根据控制流，解析activity的Bound
		ActivityInBound activityBound = new ActivityInBound();
		activityBound.getInBound();
		logger.info("activity end!");
		
		logger.info("start get service bound!");
		//根据控制流，解析activity的Bound
		ServiceInBound serviceInBound = new ServiceInBound();
		serviceInBound.getInBound();
		logger.info("service end!");
		
		logger.info("start get reciever bound!");
		//根据控制流，解析activity的Bound
		RecieverInBound recieverInBound = new RecieverInBound();
		recieverInBound.getInBound();
		logger.info("reciever end!");
		
		
//		if(CommonData.getActivityInList()==null)
//			return ;
//		for(IntentData s : CommonData.getActivityInList())
//			System.out.println(s);
		
	}
	
	
	private void getSmaliFiles(String fileRoot){
		File fileroot = new File(fileRoot);
		for(File file:fileroot.listFiles()){
			if (file.isDirectory()){
				getSmaliFiles(file.getAbsolutePath());
			}else{
				if(file.getName().endsWith(".smali"))
					smaliFileList.add(file);
			}
		}
		
	}
	
	
	private  CommonTree getCommonTree(File smaliFile, boolean verboseErrors,List<String> whiteList)
  		  throws Exception {
      CommonTokenStream tokens;

      LexerErrorInterface lexer;

      FileInputStream fis = new FileInputStream(smaliFile.getAbsolutePath());
      InputStreamReader reader = new InputStreamReader(fis, "UTF-8");

      lexer = new smaliFlexLexer(reader);
      ((smaliFlexLexer)lexer).setSourceFile(smaliFile);
      tokens = new CommonTokenStream((TokenSource)lexer);
      
  	  smaliParser parser = new smaliParser(tokens);
      parser.setVerboseErrors(verboseErrors);
//      parser.setAllowOdex(allowOdex);
//      parser.setApiLevel(apiLevel);

      smaliParser.smali_file_return result = parser.smali_file();

      if (parser.getNumberOfSyntaxErrors() > 0 || lexer.getNumberOfSyntaxErrors() > 0) {
          return null;
      }

      CommonTree t = result.getTree();

//      GetControlFlow.GetFlow(t, smaliFile.getName(),whiteList);           
      
      reader.close();
      fis.close();
      return t;

  }

	public static void main(String [] args){
		Config.setWhiteList();
//		CommonData.setDecompilePath("D:\\workspace\\RelationApk\\detemp\\2016-03-11-09-58-20\\smali\\cn\\jingling\\motu\\advertisement\\providers");
//		CommonData.setDecompilePath("D:\\workspace\\RelationApk\\detemp\\2016-04-06-13-30-10\\smali\\com\\baidu\\dq\\advertise\\d");
//		
		CommonData.setDecompilePath("D:\\git_workspace\\RelationApk\\detemp\\2016-04-26-08-59-24\\smali\\com\\zkmm\\adsdk");
		SmaliAnalyzer analyzer = new SmaliAnalyzer();
		analyzer.doSmaliAnalyse();
		
	}
}