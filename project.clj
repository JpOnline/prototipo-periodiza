(defproject pr4.core "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [tick "0.4.17-alpha"] ;; Has calendar dates, without times and timezones.
                 [re-frame "0.10.8"]
                 [day8.re-frame/re-frame-10x "0.3.3"]
                 [day8.re-frame/tracing "0.5.1"]
                 [reagent "0.8.1"]

                 ;; Dev dependencies
                 [org.clojure/test.check "0.10.0"]]

  :source-paths ["src"]

  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:cards-debug" ["trampoline" "run" "-m" "figwheel.main" "-b" "cards_debug" "-r"]
            "fig:cards-togithub"   ["run" "-m" "figwheel.main" "-O" "none" "-bo" "cards"]
            "fig:landing-dev" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:landing-prod" ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "prod"]
            }

  :profiles {:dev
             {:resource-paths ["target"]
              :clean-targets ^{:protect false} ["target"]
              :dependencies [[com.bhauman/figwheel-main "0.1.9"]
                             [com.bhauman/rebel-readline-cljs "0.1.4"]
                             [devcards "0.2.6"]]}
             :cards
             {:resource-paths ["target"]
              :clean-targets ^{:protect false} ["target"]
              :dependencies [[com.bhauman/figwheel-main "0.1.9"]
                             [com.bhauman/rebel-readline-cljs "0.1.4"]
                             [devcards "0.2.6"]]}})
