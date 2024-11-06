classDiagram
class User {
<>
+Long id
+String username
+String email
+String passwordHash
+Date createdAt
+Date updatedAt
}

class UserRepository {
    <<Repository>>
    +Optional~User~ findByUsername(String username)
    +User save(User user)
}

class UserService {
    <<Service>>
    +User registerUser(User user)
    +Optional~User~ findUserByUsername(String username)
    +User updateUserProfile(User user)
}

class UserAuthService {
    <<Service>>
    +String login(String username, String password)
    +Boolean validateToken(String token)
    +String generateToken(User user)
}

class UserController {
    <<Controller>>
    +POST /register(User user)
    +GET /profile()
}

class AuthController {
    <<Controller>>
    +POST /login(String username, String password)
    +POST /refresh-token(String token)
}

class SecurityConfig {
    <<Configuration>>
    +configure(HttpSecurity http)
}

class JwtConfig {
    <<Configuration>>
    +String secretKey
    +Long expirationTime
    +String createToken(User user)
    +Boolean isTokenValid(String token)
}

class JwtUtil {
    <<Utility>>
    +String createToken(User user)
    +String parseToken(String token)
    +Boolean isTokenValid(String token)
}

class PasswordEncoderUtil {
    <<Utility>>
    +String encodePassword(String rawPassword)
    +Boolean matches(String rawPassword, String encodedPassword)
}

class application.properties {
    <<Properties>>
    +Database configuration
    +JWT secret key
    +Redis configuration (if needed)
}

%% Relationships
UserRepository --> User : Accesses
UserService --> UserRepository : Interacts with
UserAuthService --> JwtUtil : Uses
UserAuthService --> PasswordEncoderUtil : Uses for password hashing
UserAuthService --> UserService : Uses to retrieve user data
UserController --> UserService : Calls
AuthController --> UserAuthService : Calls
SecurityConfig --> JwtConfig : Accesses
JwtUtil --> JwtConfig : Uses secret key

%% Description
note for User "Represents the user entity with fields like id, username, email, passwordHash, createdAt, and updatedAt."
note for UserRepository "Repository interface for CRUD operations on User entity."
note for UserService "Contains methods like registerUser, findUserByUsername, and updateUserProfile."
note for UserAuthService "Handles login, token generation, and token validation."
note for UserController "Exposes endpoints for registration and profile access."
note for AuthController "Exposes endpoints for login and token refreshing."
note for SecurityConfig "Configures access control and endpoint security rules."
note for JwtConfig "Handles JWT configuration, including secret key and token expiry."
note for JwtUtil "Utility class for creating, parsing, and validating JWT tokens."
note for PasswordEncoderUtil "Utility for encoding and validating passwords."
note for application.properties "Holds application-wide configurations."
