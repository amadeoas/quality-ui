package co.uk.bocaditos.ui.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.uk.bocaditos.ui.data.AbstractEntity;
import co.uk.bocaditos.ui.data.Role;

import java.util.Objects;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;


@Entity
public class User extends AbstractEntity<User> {

    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    private String profilePictureUrl;


    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(final String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(final Set<Role> roles) {
        this.roles = roles;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(final String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

	@Override
	public int hashCode() {
		return Objects.hash(this.username, this.name, this.hashedPassword, this.roles, 
				this.profilePictureUrl);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		super.toString(buf)
			.append(", username=").append(this.username)
			.append(", name=").append(this.name)
			.append(", password=<hiden>")
			.append(", profilePictureUrl=").append(this.profilePictureUrl);

		return appendEnum(buf, this.roles);
	}

	@Override
	protected boolean equalsIt(final User other) {
		return Objects.equals(this.username, other.username)
				&& Objects.equals(this.name, other.name)
				&& Objects.equals(this.hashedPassword, other.hashedPassword)
				&& Objects.equals(this.roles, other.roles)
				&& Objects.equals(this.profilePictureUrl, other.profilePictureUrl);
	}

} // end class User
