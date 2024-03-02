class User < ApplicationRecord
  has_secure_password
  has_many :movies, dependent: :destroy

  validates :username, uniqueness: true, presence: true
  validates :full_name, presence: true
  validates :password, length: { in: 6..20 }
end
