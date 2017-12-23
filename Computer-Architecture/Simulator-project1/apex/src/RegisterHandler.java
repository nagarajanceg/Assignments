/**
 * 
 */
package apex;

/**
 * @author nagaraj
 *
 */
public class RegisterHandler {
	private String name;
	private int value;
	private boolean status; //working status of register
	
	public RegisterHandler(String name, int value, boolean status) {
		this.name = name;
		this.value = value;
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RegisterHandler{" +
				"name='" + name + '\'' +
				", value=" + value +
				", status=" + status +
				'}';
	}
}
