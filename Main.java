package cn.com.address;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.itranswarp.compiler.JavaStringCompiler;

public class Main {
	private static Character pop1;

	public static void main(String[] args) {
		
		String s="F(2<1)(1-(3+2)*3)";
		System.out.println(parseF(s));
		
		
//		String expr="3*200-900/(-2*5-(-3-4)*(9+3))<1000&100<1";
		
		
		
		
		
	}
	private static Double parseF(String s){
		if(!s.contains("F")){
			return getResult(s);
		}
		//"F(1=1)(F(2>1)(3+2))";
		int index=s.indexOf("F");
		while((index=s.indexOf("F"))!=-1){
			String con=s.substring(index+2, s.indexOf(")"));
			double result = getResult(con);
			if(result>0){
				int tempIndex=s.indexOf("(",2);
				String expr=s.substring(tempIndex+1,s.lastIndexOf(")"));
				return parseF(expr);
			}else{
				return null;
			}
		}
		return null;
	}
	private static double getResult(String expr){
		Queue<String> queue=new LinkedList<String>();
		Stack<OperSign> signQueue=new Stack<OperSign>();
		Stack<String> calStack=new Stack<String>();
		List<String> result=changeExpr(expr);
		//自定义优先级（小括号）
		int customPro=0;
		for(int i=0;i<result.size();i++){
			if("(".equals(result.get(i))){
				customPro+=10;
				continue;
			}
			if(")".equals(result.get(i))){
				customPro-=10;
				continue;
			}
			OperSign operSign=toOperSign(result.get(i)+"");
			if(operSign==null){
				queue.add(result.get(i)+"");
				continue;
			}
			operSign.setPrority(operSign.getPrority()+customPro);
			//
			if(signQueue.size()==0){
				signQueue.add(operSign);
				continue;
			}
			//将优先级高的出栈
			while(signQueue.size()>0){
				OperSign pop = signQueue.pop();
				if(pop.getPrority()<operSign.getPrority()){
					signQueue.add(pop);
					break;
				}
				queue.add(pop.getOperSign()+"");
			}
			signQueue.add(operSign);
		}
		while(signQueue.size()!=0){
			queue.add(signQueue.pop().getOperSign()+"");
		}
		System.out.println(queue);
		//计算
		String temp=null;
		while((temp=queue.poll())!=null){
			OperSign operSign = toOperSign(temp);
			if(operSign==null){
				calStack.add(temp);
				continue;
			}
			cal(operSign,calStack);
		}
		return new Double(calStack.pop());
	}
	private static List<String> changeExpr(String expr) {
		List<String> result=new ArrayList<String>();
		Pattern compile = Pattern.compile("\\d+(\\.\\d+)?");
		Matcher matcher = compile.matcher(expr);
		int start =0;
		while(matcher.find()){
			int flag=0;
			for(;start<matcher.start();start++){
				String temp=expr.charAt(start)+"";
				if(toOperSign(temp)!=null){
					flag++;
				}
				if(flag==2){
					result.add("0");
				}
				result.add(temp);
			}
			start=matcher.end();
			String group = matcher.group();
			result.add(group);
		}
		
//		Matcher matcher=m
		return result;
	}

	private static void cal(OperSign c, Stack<String> calStack) {
		String pop2 = calStack.pop();
		String pop1="0";
		if(calStack.size()>0){
			pop1=calStack.pop();
		} 
		Double b=null;
		
		if("+".equals(c.getOperSign())){
			b=new Double(pop1)+new Double(pop2);
		}
		if("-".equals(c.getOperSign())){
			b=new Double(pop1)-new Double(pop2);
		}
		if("*".equals(c.getOperSign())){
			b=new Double(pop1)*new Double(pop2);
		}
		if("/".equals(c.getOperSign())){
			b=new Double(pop1)/new Double(pop2);
		}
		if(">".equals(c.getOperSign())){
			b=0.0;
			if(new Double(pop1)>new Double(pop2)){
				b=1.0;
			}
		}
		if("<".equals(c.getOperSign())){
			b=0.0;
			if(new Double(pop1)<new Double(pop2)){
				b=1.0;
			}
		}
		if("=".equals(c.getOperSign())){
			b=0.0;
			if(new Double(pop1)==new Double(pop2)){
				b=1.0;
			}
		}
		if("&".equals(c.getOperSign())){
			b=0.0;
			if(new Double(pop1)>0&&new Double(pop2)>0){
				b=1.0;
			}
		}
		if("|".equals(c.getOperSign())){
			b=0.0;
			if(new Double(pop1)+new Double(pop2)>0){
				b=1.0;
			}
		}

		calStack.add(b+"");
	}

//	private static OperSign toOperSign(char c) {
//		if(c=='+'){
//			return new OperSign(c,1);
//		}
//		if(c=='-'){
//			return new OperSign(c,1);
//		}
//		if(c=='*'){
//			return new OperSign(c,2);
//		}
//		if(c=='/'){
//			return new OperSign(c,2);
//		}
//		return null;
//	}
	private static OperSign toOperSign(String c) {
		if("+".equals(c)){
			return new OperSign(c,1);
		}
		if("-".equals(c)){
			return new OperSign(c,1);
		}
		if("*".equals(c)){
			return new OperSign(c,2);
		}
		if("/".equals(c)){
			return new OperSign(c,2);
		}
		if(">".equals(c)){
			return new OperSign(c,0);
		}
		if("<".equals(c)){
			return new OperSign(c,0);
		}
		if("&".equals(c)){
			return new OperSign(c,-1);
		}
		if("|".equals(c)){
			return new OperSign(c,-1);
		}
		if("=".equals(c)){
			return new OperSign(c,0);
		}
		return null;
	}

}
class OperSign{
	private String operSign;
	private Integer prority;
	OperSign(String operSign,Integer prority){
		this.operSign=operSign;
		this.prority=prority;
	}
	public String getOperSign() {
		return operSign;
	}
	public void setOperSign(String operSign) {
		this.operSign = operSign;
	}
	public Integer getPrority() {
		return prority;
	}
	public void setPrority(Integer prority) {
		this.prority = prority;
	}
	
}
