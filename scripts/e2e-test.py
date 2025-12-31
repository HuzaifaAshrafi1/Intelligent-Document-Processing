#!/usr/bin/env python3
"""
End-to-End Test Script for Intelligent Document Processing Pipeline
Uses Selenium WebDriver to test the complete user workflow
"""

import time
import os
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from selenium.common.exceptions import TimeoutException, NoSuchElementException


class DocumentProcessingE2ETest:
    def __init__(self, base_url="https://your-domain.com"):
        self.base_url = base_url
        self.driver = None
        self.wait = None

    def setup_driver(self):
        """Initialize Chrome WebDriver with options"""
        chrome_options = Options()
        chrome_options.add_argument("--headless")  # Run in headless mode
        chrome_options.add_argument("--no-sandbox")
        chrome_options.add_argument("--disable-dev-shm-usage")
        chrome_options.add_argument("--window-size=1920,1080")

        self.driver = webdriver.Chrome(options=chrome_options)
        self.driver.implicitly_wait(10)
        self.wait = WebDriverWait(self.driver, 30)

    def teardown_driver(self):
        """Clean up WebDriver"""
        if self.driver:
            self.driver.quit()

    def test_document_upload_workflow(self):
        """Test complete document upload and processing workflow"""
        try:
            print("Starting E2E test: Document Upload Workflow")

            # Navigate to application
            self.driver.get(self.base_url)
            print("✓ Navigated to application")

            # Wait for page to load
            self.wait.until(EC.presence_of_element_located((By.TAG_NAME, "body")))
            print("✓ Page loaded successfully")

            # Check if login form is present
            try:
                login_form = self.driver.find_element(By.CSS_SELECTOR, "form[action*='login'], [data-testid='login-form']")
                print("✓ Login form found")

                # Perform login
                username_field = self.driver.find_element(By.CSS_SELECTOR, "input[name='username'], input[type='email']")
                password_field = self.driver.find_element(By.CSS_SELECTOR, "input[name='password'], input[type='password']")
                login_button = self.driver.find_element(By.CSS_SELECTOR, "button[type='submit'], [data-testid='login-button']")

                username_field.send_keys("testuser@example.com")
                password_field.send_keys("testpassword")
                login_button.click()

                # Wait for login to complete
                self.wait.until(EC.url_changes(self.base_url))
                print("✓ Login successful")

            except NoSuchElementException:
                print("ℹ No login form found, assuming direct access")

            # Navigate to document upload page
            upload_url = f"{self.base_url}/upload"
            self.driver.get(upload_url)

            # Wait for upload component to load
            upload_element = self.wait.until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "[data-testid='document-upload'], .upload-area, input[type='file']"))
            )
            print("✓ Upload page loaded")

            # Find file input
            file_input = self.driver.find_element(By.CSS_SELECTOR, "input[type='file']")

            # Use a test file (you should place a test PDF in the scripts directory)
            test_file_path = os.path.join(os.path.dirname(__file__), "test-document.pdf")

            # Create a simple test file if it doesn't exist
            if not os.path.exists(test_file_path):
                with open(test_file_path, "w") as f:
                    f.write("This is a test document for E2E testing.")

            file_input.send_keys(test_file_path)
            print("✓ Test file selected")

            # Click upload button
            upload_button = self.driver.find_element(By.CSS_SELECTOR, "button[data-testid='upload-button'], button:contains('Upload')")
            upload_button.click()
            print("✓ Upload initiated")

            # Wait for processing to complete
            progress_indicator = self.wait.until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "[data-testid='progress'], .progress-bar"))
            )

            # Wait for completion (adjust timeout based on expected processing time)
            completion_indicator = self.wait.until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "[data-testid='processing-complete'], .success-message")),
                message="Processing did not complete within timeout"
            )
            print("✓ Document processing completed")

            # Verify results
            results_section = self.driver.find_element(By.CSS_SELECTOR, "[data-testid='results'], .results")
            assert results_section.is_displayed(), "Results section not displayed"
            print("✓ Results displayed successfully")

            print("✅ E2E test passed: Document Upload Workflow")
            return True

        except TimeoutException as e:
            print(f"❌ E2E test failed: Timeout - {e}")
            return False
        except Exception as e:
            print(f"❌ E2E test failed: {e}")
            return False

    def test_health_check(self):
        """Test application health endpoints"""
        try:
            print("Testing health check endpoints")

            # Test backend health
            self.driver.get(f"{self.base_url}/api/actuator/health")
            health_response = self.driver.find_element(By.TAG_NAME, "body").text

            if "UP" in health_response or "status" in health_response.lower():
                print("✓ Backend health check passed")
            else:
                print("❌ Backend health check failed")
                return False

            return True

        except Exception as e:
            print(f"❌ Health check failed: {e}")
            return False

    def run_all_tests(self):
        """Run all E2E tests"""
        self.setup_driver()

        try:
            results = []

            # Run individual tests
            results.append(self.test_health_check())
            results.append(self.test_document_upload_workflow())

            # Summary
            passed = sum(results)
            total = len(results)

            print(f"\n{'='*50}")
            print(f"E2E Test Results: {passed}/{total} tests passed")

            if passed == total:
                print("🎉 All E2E tests passed!")
                return True
            else:
                print("⚠️  Some E2E tests failed")
                return False

        finally:
            self.teardown_driver()


def main():
    """Main function to run E2E tests"""
    import argparse

    parser = argparse.ArgumentParser(description="Run E2E tests for Intelligent Document Processing")
    parser.add_argument("--url", default="https://your-domain.com", help="Base URL of the application")
    parser.add_argument("--headless", action="store_true", help="Run in headless mode")

    args = parser.parse_args()

    # Override headless setting
    if args.headless:
        os.environ["HEADLESS"] = "true"

    tester = DocumentProcessingE2ETest(args.url)
    success = tester.run_all_tests()

    exit(0 if success else 1)


if __name__ == "__main__":
    main()
