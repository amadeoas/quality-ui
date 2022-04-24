package co.uk.bocaditos.ui.data.entity;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import co.uk.bocaditos.ui.data.AbstractEntity;


public class SamplePerson extends AbstractEntity<SamplePerson> {

	private Title title;

	@Pattern(regexp="^[a-zA-Z\\s]{2,20}$")
	@Size(min=1, max=20)
    private String firstName;

	@Pattern(regexp="^[a-zA-Z\\s]{2,30}$")
	@Size(min=1, max=30)
	private String lastName;

	@Email(message="Please enter a valid email address")
	private String email;

	@Pattern(regexp="^\\d{11,11}$")
	@Size(min=11, max=11)
	private String phone;

	@Pattern(regexp="^[\\d-]{12,12}$")
	@Size(min=11, max=11)
	private LocalDate dateOfBirth;

	@Pattern(regexp="^[a-zA-Z\\s]{2,40}$")
	@Size(min=1, max=40)
	private String occupation;
	
	private SampleAddress address;

	private boolean important;

	
	public SamplePerson() {
		this.address = new SampleAddress();
	}

	public Title getTitle() {
		return this.title;
	}

	public void setTitle(final Title title) {
		this.title = title;
	}

	public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
    
    public SampleAddress getAddress() {
    	return this.address;
    }
    
    public void setAddress(final SampleAddress address) {
    	this.address = address;
    }

	@Override
	public int hashCode() {
		return Objects.hash(dateOfBirth, email, firstName, important, lastName, occupation, phone, 
				title, this.address);
	}

	@Override
	protected boolean equalsIt(final SamplePerson other) {
		return Objects.equals(this.dateOfBirth, other.dateOfBirth)
				&& Objects.equals(this.email, other.email)
				&& Objects.equals(this.firstName, other.firstName)
				&& Objects.equals(this.important, other.important)
				&& Objects.equals(this.lastName, other.lastName)
				&& Objects.equals(this.occupation, other.occupation)
				&& Objects.equals(this.phone, other.phone)
				&& Objects.equals(this.title, other.title)
				&& Objects.equals(this.address, other.address);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		return super.toString(buf)
			.append(", title=").append(this.title)
			.append(", firstName=").append(this.firstName)
			.append(", lastName=").append(this.lastName)
			.append(", email=").append(this.email)
			.append(", phone=").append(this.phone)
			.append(", dateOfBirth=").append(this.dateOfBirth)
			.append(", occupation=").append(this.occupation)
			.append(", important=").append(this.important)
			.append(", address=").append(this.address);
	}

} // end class SamplePerson
