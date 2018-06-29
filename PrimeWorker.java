import java.util.ArrayList;

import akka.actor.UntypedActor;


public class PrimeWorker extends UntypedActor{
	public void onReceive(Object message){
		if(message instanceof int[]){
			int messages[] = (int []) message;
			int start=messages[0];
			int end=messages[1];
			
			ArrayList<Integer> result = new ArrayList<Integer>();
			for(int i = start; i <=end; i++)
				if(isPrime(i))
					result.add(i);
			
			getSender().tell(result, getSelf());
		}
		else {
			unhandled(message);
		}
	}
	
	private static boolean isPrime(int n){
		if(n == 1)
			return false;
		else if(n == 2)
			return true;
		
		if(n % 2 == 0)
			return false;
		
		for(int i=3; i*i<=n; i+=2)
			if(n % i == 0)
				return false;
		return true;
		
	}
}
