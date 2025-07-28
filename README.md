# Spring Boot Starter Application

This is a starter Spring Boot application with various features including OAuth2 authentication, JWT token-based security, and more.

## Environment Variables

The application uses environment variables for configuration. You can set these environment variables in your system.

### Setting Up Environment Variables

You can set the environment variables in your system:

- Windows:
  ```
  set GOOGLE_CLIENT_ID=your-google-client-id
  set GOOGLE_CLIENT_SECRET=your-google-client-secret
  ```

- Linux/macOS:
  ```
  export GOOGLE_CLIENT_ID=your-google-client-id
  export GOOGLE_CLIENT_SECRET=your-google-client-secret
  ```

### Available Environment Variables

Here are the environment variables used in the application:

#### OAuth2 Client Credentials

- `GOOGLE_CLIENT_ID`: Google OAuth2 client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth2 client secret
- `GITHUB_CLIENT_ID`: GitHub OAuth2 client ID
- `GITHUB_CLIENT_SECRET`: GitHub OAuth2 client secret

#### Database Configuration

- `SPRING_DATASOURCE_URL`: Database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

#### JWT Configuration

- `JWT_AES_KEY`: JWT AES key
- `JWT_AES_IV`: JWT AES initialization vector

#### Mail Configuration

- `SPRING_MAIL_USERNAME`: Email username
- `SPRING_MAIL_PASSWORD`: Email password

#### AWS Configuration

- `AWS_S3_ACCESS_KEY`: AWS S3 access key
- `AWS_S3_SECRET_KEY`: AWS S3 secret key

## Running the Application

To run the application, use the following command:

```bash
./mvnw spring-boot:run
```

## Features

- OAuth2 authentication with Google and GitHub
- JWT token-based security
- Role-based access control
- User management
- Email sending
- File upload to AWS S3
- Redis caching
- PostgreSQL database
- Flyway database migrations
- Internationalization
- Swagger API documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.
