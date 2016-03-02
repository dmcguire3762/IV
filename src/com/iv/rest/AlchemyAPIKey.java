package com.iv.rest;

public class AlchemyAPIKey {
	private final String key;
	private int transactionCount = 0;
	
	public AlchemyAPIKey(String key){
		this.key = key;
	}
	
	public void addTransactions(int num){
		transactionCount += num;
	}
	
	public int getTransactionCount(){
		return transactionCount;
	}
	
	public String getKey(){
		return key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlchemyAPIKey other = (AlchemyAPIKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
