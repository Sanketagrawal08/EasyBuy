package in.divyansh.exception;

public class ResourceNotFoundException extends RuntimeException{
	private final String resourceName;
	private final String fieldName;
	private final Object fieldlValue;
	
	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldlValue) {
		super(String.format("%s not found with %s:%s",resourceName,fieldName,fieldlValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldlValue = fieldlValue;
	}
	public ResourceNotFoundException(String message) {
		super(message);
		this.resourceName = null;
		this.fieldName = null;
		this.fieldlValue = null;
	}
	
	

}
