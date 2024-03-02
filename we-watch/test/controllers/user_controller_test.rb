require "test_helper"

class UserControllerTest < ActionDispatch::IntegrationTest
  test "should get sign_up" do
    get user_sign_up_url
    assert_response :success
  end

  test "should get login" do
    get user_login_url
    assert_response :success
  end
end
