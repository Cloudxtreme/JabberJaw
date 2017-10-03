import java.util.Arrays;
import java.util.Stack;

public class GrammerReader {
	private Stack CrudeGrammerCheck(String input)
	{
		Stack<Object> stack = new Stack<Object>();
			String phrase = input;
			String[]  groups= phrase.split(" ");
			for(int i = 0; i < groups.length; i++)
			{
				stack.push(groups[i]);
				while(!stack.isEmpty() && !(stack.peek() instanceof  Sentence))
				{
					//how to account for size
					Object top = stack.pop();
					
					if(top instanceof  VerbPhrase && !stack.isEmpty() && stack.peek() instanceof NounPhrase)
					{
						Sentence sentence = new Sentence();
						sentence.setNounPhrase((NounPhrase)stack.peek());
						sentence.setVerbPhrase((VerbPhrase)top);
						stack.pop(); // get rid of the NounPhrase
						stack.push(sentence);
					}else if(top instanceof Noun && !stack.isEmpty() && stack.peek() instanceof Article && ((i + 1) >= groups.length || !groups[i+1].equals("with")))
					{
						NounPhrase nounPhrase = new NounPhrase();
						nounPhrase.setAtricle((Article)stack.peek());
						nounPhrase.setNoun((Noun)top);
						stack.pop(); // get rid of Article
						stack.push(nounPhrase);
					}else if(checkPrep(top, stack))//here change
					{
						NounPhrase nounPhrase = new NounPhrase();
						Preposition prep = (Preposition)stack.pop();
						nounPhrase.setNoun((Noun)stack.pop());
						nounPhrase.setAtricle((Article)stack.pop());
						
						prep.setPhrase((NounPhrase)top);
						nounPhrase.setPrep(prep);
						
						stack.push(nounPhrase);
					}else if(top instanceof Verb && ((i + 1) >= groups.length || !Arrays.asList("a", "the", "dog", "cat", "fish").contains((groups[i+1]) )))
					{
						VerbPhrase verbPhrase = new VerbPhrase();
						//Verb verb = new Verb((String)top);
						verbPhrase.setVerb((Verb)top);
						stack.push(verbPhrase);
					}else if(top instanceof NounPhrase && !stack.isEmpty() && stack.peek() instanceof Verb)
					{
						VerbPhrase verbPhrase = new VerbPhrase();
						verbPhrase.setVerb((Verb)stack.pop());
						verbPhrase.setNounPhrase((NounPhrase)top);
						stack.push(verbPhrase);
					}else if(top instanceof String)
					{
						String word = (String)top;
						if(word.equals("a")||word.equalsIgnoreCase("the"))
						{
							Article art  = new Article(word);
							stack.push(art);
						}else if(word.equals("dog")||word.equals("cat")||word.equals("fish") )
						{
							Noun noun = new Noun(word);
							stack.push(noun);
						}else if(word.equals("with"))
						{
							Preposition noun = new Preposition(word);
							stack.push(noun);
						}else if(word.equals("bites")||word.equals("chases"))
						{
							Verb verb = new Verb(word);
							stack.push(verb);
						}
					}else if(top instanceof String && !stack.isEmpty() && stack.peek() instanceof NounPhrase)
					{
						Preposition prep = new Preposition();
						prep.setPrep((String)stack.peek());
						prep.setPhrase((NounPhrase)top);
					}
					else {
						stack.push(top);
						break;
					}
				}
			}
			if(!stack.isEmpty())
			{
				System.out.println("Below Should be 1 and the original sentence"
						+"\n"+"Stack size: "+stack.size() + " Stack item: "+stack.pop());
						//this last pop actually empties out the sentence confirming the completion
			}
			else
			{
				System.out.println("Direct failure: Input does not match Grammer");
				stack.add("failure");
			}
		return stack;
	}
	public boolean IsinGrammer(String input)
	{
		Stack stack = CrudeGrammerCheck(input);
		//can change this later with a mmore refined graammer
		if(stack.isEmpty())
		{
			//passes as part of the grammer 
			return true;
		}
		else
		{
			//fails grammer test
			return false;
		}
	}
	private  static boolean checkPrep(Object top,Stack<Object> stack)
	{
		if(stack.empty())
		{
			return false;
		}
		Object piece2 = stack.pop();//only comes off if not empty
		if(stack.empty())
		{
			stack.push(piece2);
			return false;
		}
		Object piece3 = stack.pop();//only comes off if not empty
		if(stack.empty())
		{
			stack.push(piece3);
			stack.push(piece2);
			return false;
		}
		Object piece4 = stack.pop();//only comes off if not empty
		boolean passed = false;
		if(top instanceof NounPhrase && piece2 instanceof Preposition && piece3 instanceof Noun &&  piece4 instanceof Article )
		{
			passed = true;
		}
		stack.push(piece4);
		stack.push(piece3);
		stack.push(piece2);
		return passed;
	}
}
