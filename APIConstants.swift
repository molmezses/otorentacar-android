//
//  APIConstants.swift
//  SmartBeautyIOS
//
//  Created by MERT MUTLU on 22.04.2025.
//

import Foundation

enum APIConstants {
    static let baseURL = "https://api.bluesense.ai"
    static let communityBaseURL = "https://community.bluesense.ai/api/v1/community"

    // MARK: - Address Management
    enum Address {
        static let getAll = baseURL + "/api/v1/addresses"
        static let addAddress = baseURL + "/api/v1/addresses"
        static func getAddressById(_ id: Int) -> String {
            return baseURL + "/api/v1/addresses/\(id)"
        }
        
        static func deleteAddressById(_ id: Int) -> String {
            return baseURL + "/api/v1/addresses/\(id)"
        }
        
        static func edaadress(_ id: Int) -> String {
            return baseURL + "/api/v1/addresses/\(id)"
        }
    }
    
    // MARK: - Authentication
    enum Auth {
        static let login = baseURL + "/api/v1/auth/login"
        static let refreshToken = baseURL + "/api/v1/auth/refresh-token"
        static let validateEmailWithCode = baseURL + "/api/v1/auth/password/validate-email-with-code"
        static let verifyCode = baseURL + "/api/v1/auth/password/verify-code"
        static let resetPassword = baseURL + "/api/v1/auth/password/reset"
        static let appleSignIn = baseURL + "/api/v1/auth/apple"
        static let googleSignIn = baseURL + "/api/v1/auth/google"
    }

    // MARK: - Shopping Cart
    enum Cart {
        static let getCart = baseURL + "/api/v1/cart"
        static let deleteCart = baseURL + "/api/v1/cart"
        static let clearCart = baseURL + "/api/v1/cart/clear"
        static let addItem = baseURL + "/api/v1/cart/item"
        
        static func updateItemQuantity(_ id: Int) -> String {
            return baseURL + "/api/v1/cart/item/\(id)"
        }
        static func removeItem(_ id: Int) -> String {
            return baseURL + "/api/v1/cart/item/\(id)"
        }
        static func incrementItem(_ id: Int) -> String {
            return baseURL + "/api/v1/cart/item/\(id)/increment"
        }
        static func decrementItem(_ id: Int) -> String {
            return baseURL + "/api/v1/cart/item/\(id)/decrement"
        }
    }

    // MARK: - Discount Management
    enum DiscountCode {
        static let apply = baseURL + "/api/v1/discount/apply"
        static let active = baseURL + "/api/v1/discount/active"
        static let cancel = baseURL + "/api/v1/discount/cancel"
    }

    // MARK: - Orders and Checkout
    enum Order {
        static let getAllOrders = baseURL + "/api/v1/orders"
        static let buyItAgain = baseURL + "/api/v1/orders/buy-again"
        static let preCheckout = baseURL + "/api/v1/orders/pre-checkout"
        static let checkout = baseURL + "/api/v1/orders/checkout"
        static let testCheckout = baseURL + "/api/v1/orders/test/checkout"
    }

    // MARK: - Product Management
    enum Product {
        static let getLatestProducts = baseURL + "/api/v1/product/latest"
        static let getLatestProductsByMyCountry = baseURL + "/api/v1/product/latest-by-my-country"
        static let getProductsByUserCountry = baseURL + "/api/v1/product/by-my-country"
        
        static func getProduct(_ id: Int) -> String {
            return baseURL + "/api/v1/product/\(id)"
        }
        static func getProductsByCountry(_ countryCode: String) -> String {
            return baseURL + "/api/v1/product/by-country/\(countryCode)"
        }
    }

    // MARK: - Product Reviews
    enum ProductReviews {
        static func getReviewsByProduct(_ productId: Int) -> String {
            return baseURL + "/api/v1/product/reviews/product/\(productId)"
        }
        static let createReview = baseURL + "/api/v1/product/reviews"
        static let getAllReviews = baseURL + "/api/v1/product/reviews"
        static func getReviewById(_ reviewId: Int) -> String {
            return baseURL + "/api/v1/product/reviews/\(reviewId)"
        }
        static func updateReview(_ reviewId: Int) -> String {
            return baseURL + "/api/v1/product/reviews/\(reviewId)"
        }
        static func deleteReview(_ reviewId: Int) -> String {
            return baseURL + "/api/v1/product/reviews/\(reviewId)"
        }
    }

    // MARK: - Skin Analysis Operations
    enum SkinAnalysis {
        static let analyzeFaceUnified = baseURL + "/api/SkinAnalysis/analyze-face" // POST endpoint for image upload
        static let acne = baseURL + "/api/v1/skin-analysis/acne"
        static let age = baseURL + "/api/v1/skin-analysis/age"
        static let eyebag = baseURL + "/api/v1/skin-analysis/eyebag"
        static let eyelid = baseURL + "/api/v1/skin-analysis/eyelid"
        static let firmness = baseURL + "/api/v1/skin-analysis/firmness"
        static let moisture = baseURL + "/api/v1/skin-analysis/moisture"
        static let oiliness = baseURL + "/api/v1/skin-analysis/oiliness"
        static let pigmentation = baseURL + "/api/v1/skin-analysis/pigmentation"
        static let redness = baseURL + "/api/v1/skin-analysis/redness"
        static let texture = baseURL + "/api/v1/skin-analysis/texture"        // ✨ New in v1
        static let uvDamage = baseURL + "/api/v1/skin-analysis/uv-damage"     // ✨ New in v1
        static let wrinkle = baseURL + "/api/v1/skin-analysis/wrinkle"
        
        // Legacy endpoints (deprecated - will be removed)
        static let latestAll = baseURL + "/api/v1/skin-analysis/latest-all"
        static let secondLatestAll = baseURL + "/api/v1/skin-analysis/second-latest-all"
        static let historyDates = baseURL + "/api/v1/skin-analysis/history/dates"
        
        static func getLatestAnalysisByDate(_ date: String) -> String {
            return baseURL + "/api/v1/skin-analysis/history/latest/bydate/\(date)"
        }
        
        // New session-based endpoints
        static func sessions(limit: Int = 10) -> String {
            return baseURL + "/api/v1/skin-analysis/sessions?limit=\(limit)"
        }
        
        static func sessionsDetails(sessionIds: [String]) -> String {
            // Join sessionIds with comma (no need to URL encode, Alamofire handles it)
            let idsString = sessionIds.joined(separator: ",")
            return baseURL + "/api/v1/skin-analysis/sessions/details?sessionIds=\(idsString)"
        }
        
        static let latestSession = baseURL + "/api/v1/skin-analysis/latest-session"
    }

    // MARK: - Notifications
    enum Notification {
        static let sendPhoto = baseURL + "/api/v1/notifications/send-photo"
        static let registerFCM = baseURL + "/api/v1/notifications/register-fcm"
        static let sendIOS = baseURL + "/api/v1/notifications/ios"
        static let sendAll = baseURL + "/api/v1/notifications/all"
        static let sendEmail = baseURL + "/api/v1/notifications/email"
        static let getNotifications = baseURL + "/api/v1/notifications/user"
    }

    // MARK: - User Favorites
    enum Favorites {
        static let getFavorites = baseURL + "/api/v1/favorites"
        
        static func addFavorite(_ productId: Int) -> String {
            return baseURL + "/api/v1/favorites/\(productId)"
        }
        static func removeFavorite(_ productId: Int) -> String {
            return baseURL + "/api/v1/favorites/\(productId)"
        }
    }

    // MARK: - User Profile
    enum Customer {
        static let createCustomer = baseURL + "/api/v1/customer"
        static let updateInfo = baseURL + "/api/v1/customer/update-info"
        static let deleteAccount = baseURL + "/api/v1/customer/delete-account"
        static let setCountry = baseURL + "/api/v1/customer/country"
        static let getCountry = baseURL + "/api/v1/customer/country"
        static let getTermsStatus = baseURL + "/api/v1/customer/terms-status"
        static let setTermsStatus = baseURL + "/api/v1/customer/terms-status"
        static let setBirthday = baseURL + "/api/v1/customer/birthday"
        static let getBirthday = baseURL + "/api/v1/customer/birthday"
        static let uploadProfilePhoto = baseURL + "/api/v1/customer/profile-photo"
    }

    // MARK: - User Preferences
    enum UserPreferences {
        static let createBeautyPreferences = baseURL + "/api/v1/customer/beauty-preferences"
        static let createBeautyCenterPreferences = baseURL + "/api/v1/customer/beauty-center-preferences"
        static let createDermatologistPreferences = baseURL + "/api/v1/customer/dermatologist-preferences"
        static let postBeautyPreferences = baseURL + "/api/v1/customer/beauty-preferences" // For pre-skin analysis questionnaire
    }
    
    // MARK: - Articles
    enum Articles {
        static let getAllArticles = baseURL + "/api/v1/articles"
        static let getAllVideos = baseURL + "/api/v1/articles/videos"
        static func getArticleById(_ id: Int) -> String {
            return baseURL + "/api/v1/articles/\(id)"
        }
    }

    // MARK: - User Recommendations
    enum Recommendations {
        static let byAnalysisCountry = baseURL + "/api/v1/customer/recommendations/by-analysis-country"
        static let byPreferencesCountry = baseURL + "/api/v1/customer/recommendations/by-preferences-country"
    }

    // MARK: - Version
    enum Version {
        static let checkIOS = baseURL + "/api/v1/version/ios/check"
        static let configuration = baseURL + "/api/v1/version/configuration"
    }

    // MARK: - ChatBot
    enum ChatBot {
        static let sendMessage = baseURL + "/api/v1/chatbot/message"
    }
    
    // MARK: - Affiliate Products
    enum AffiliateProduct {
        static func getProducts(lang: String, country: String, query: String?, page: Int, pageSize: Int) -> String {
            var url = "\(baseURL)/api/v1/affiliate-products?lang=\(lang)&country=\(country)&page=\(page)&pageSize=\(pageSize)"
            if let query = query, !query.isEmpty {
                url += "&q=\(query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query)"
            }
            return url
        }
        
        static func getProduct(id: Int, lang: String, country: String) -> String {
            return "\(baseURL)/api/v1/affiliate-products/\(id)?lang=\(lang)&country=\(country)"
        }
    }
    
    // MARK: - Community
    enum Community {
        // Profile
        static let onboarding    = communityBaseURL + "/profiles/onboarding"
        static let myProfile     = communityBaseURL + "/profiles/me"
        static let createProfile = communityBaseURL + "/profiles"
        
        // File Upload
        static let fileUpload    = communityBaseURL + "/files/upload"

        // Update Profile
        static let updateProfile = communityBaseURL + "/profiles/me"
        
        //Follow
        static let followers = communityBaseURL + "/follows/followers"
        static let following = communityBaseURL + "/follows/following"
        
        static func followUser(_ username: String) -> String {
            return communityBaseURL + "/follows/\(username)"
        }
        
        static let incomingRequests  = communityBaseURL + "/follows/requests/incoming"
        static func acceptRequest(_ username: String) -> String {
            return communityBaseURL + "/follows/requests/\(username)/accept"
        }
        static func rejectRequest(_ username: String) -> String {
            return communityBaseURL + "/follows/requests/\(username)/reject"
        }
        
        static let feed = communityBaseURL + "/feed"
        
        static let myPosts = communityBaseURL + "/posts/me"
        
        static let mustafa = communityBaseURL + "/posts/me"

        static let eda = communityBaseURL + "/posdasadsts/asd"

        
        static let ela = communityBaseURL + "/possadsadasdts/me"

        
        static let volkanhoca  = communityBaseURL + "/posts/me"

    }
    
    
}
