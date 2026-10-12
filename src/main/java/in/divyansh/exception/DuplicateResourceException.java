package in.divyansh.exception;

public class DuplicateResourceException extends RuntimeException {
	private final String resourceName;
	private final String fieldName;
	private final Object fieldlValue;
	
	public DuplicateResourceException(String resourceName, String fieldName, Object fieldlValue) {
		super(String.format("%s already exists with %s:%s",resourceName,fieldName,fieldlValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldlValue = fieldlValue;
	}
	public DuplicateResourceException(String message) {
		super(message);
		this.resourceName = null;
		this.fieldName = null;
		this.fieldlValue = null;
	}
	
}
