class LoginPage {
  constructor(page) {
    this.page = page;
    this.loginWithPasswordOption = page.getByText('Login with Password');
    this.emailInput = page.getByPlaceholder('Email address');
    this.passwordInput = page.getByPlaceholder('Password');
    this.loginButton = page.getByRole('button', { name: 'Log In' });
    this.homePageIndicator = page.getByText('Home');
    this.errorMessage = page.locator('[data-testid="error-message"]');
    this.forgotPasswordLink = page.getByText('Forgot Password');
  }

  async navigateToLogin() {
    await this.page.goto('/login');
  }

  async clickLoginWithPassword() {
    await this.loginWithPasswordOption.click();
  }

  async fillEmail(email) {
    await this.emailInput.fill(email);
  }

  async fillPassword(password) {
    await this.passwordInput.fill(password);
  }

  async clickLoginButton() {
    await this.loginButton.click();
  }

  async login(email, password) {
    await this.clickLoginWithPassword();
    await this.fillEmail(email);
    await this.fillPassword(password);
    await this.clickLoginButton();
  }

  async isHomePageLoaded() {
    return await this.homePageIndicator.isVisible();
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }

  async isErrorMessageVisible() {
    return await this.errorMessage.isVisible();
  }
}

module.exports = LoginPage;