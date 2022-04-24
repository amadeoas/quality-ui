package co.uk.bocaditos.ui.data.entity;

import java.util.Objects;

import co.uk.bocaditos.ui.data.AbstractEntity;


public class SampleAddress extends AbstractEntity<SampleAddress> {

    private String street;
    private String postalCode;
    private String city;
    private String state;
    private String country;


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	@Override
	public int hashCode() {
		return Objects.hash(this.street, this.postalCode, this.city, this.state, this.country);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		return super.toString(buf)
				.append("street=").append(this.street)
				.append("postalCode=").append(this.postalCode)
				.append("city=").append(this.city)
				.append("state=").append(this.state)
				.append("country=").append(this.country);
	}

	@Override
	protected boolean equalsIt(final SampleAddress other) {
		return Objects.equals(this.street, other.street)
				&& Objects.equals(this.postalCode, other.postalCode)
				&& Objects.equals(this.city, other.city)
				&& Objects.equals(this.state, other.state)
				&& Objects.equals(this.country, other.country);
	}

} // end class SampleAddress
