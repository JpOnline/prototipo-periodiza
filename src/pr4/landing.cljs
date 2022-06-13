(ns pr4.landing
  (:require
    [pr4.components.sessions-chart :refer [sessions-chart]]
    [pr4.components.macro-timeline :refer [macro-timeline]]
    [pr4.components.redefine-periodization :refer [redefine-periodization]]
    ))

(defn main []
  [:div#app-container
   {:style #js {:display "flex"
                :flexDirection "column"}}
   [:div#top-panel
    {:style #js {:height "90vh"
                 :width "100vw"
                 :zIndex -2
                 :display "grid"
                 :grid "1fr 2fr 1fr 1fr / 1fr"}}
    [:style
     "@import url('https://fonts.googleapis.com/css?family=Mansalva&display=swap');"]
    [:svg
     {:height "90vh"
      :width "100vw"
      :border "solid yellow"
      :style #js {:position "absolute"
                  :zIndex "-1"}}
     [:rect
      {:width "100vw"
       :height "90vh"
       :style #js {:fill "green"}}]
     [:ellipse
      {:cx "50vw"
       :cy "90vh"
       :rx "58vw"
       :ry "calc(50px + 2vw)"
       :style #js {:fill "white"}}]]
    [:img
     {:src "images/Periodiza_logo-simbolo-e-nome.svg"
      :style #js {:padding "3vh"
                  :width "29vw"}}]
    [:div
     {:style #js {:width "25%" ;; Use this one to scale.
                  :paddingLeft "50vw"
                  :position "absolute"
                  :zIndex -1}}
     [:div
      {:style #js {:backgroundImage "url(images/Periodiza_logo.svg)"
                   :width "100%"
                   :height "0"
                   :backgroundSize "cover"
                   :padding "0 0 100% 0"
                   ;; :transform "rotate(64deg)"
                   :opacity 0.5}}]]
    [:h1
     {:style #js {:color "white"
                  :gridArea "2 / 1 / 2 / 1"
                  :fontFamily "Mansalva, serif"
                  :fontSize "2.1em"
                  :paddingLeft "2vw"
                  }}
     "Essa é uma ferramenta para criar um planejamento"
     [:br]
     [:span
      {:style #js {:padding "4vh 0 0 6vw"
                   :display "inline-block"}}
      "para extrair mais resultados de seus atletas e alunos."]]
    ;; [:h1
    ;;  {:style #js {:textAlign "center"
    ;;               :color "white"
    ;;               :gridArea "3 / 1 / 3 / 1"
    ;;               :fontFamily "Mansalva, serif"
    ;;               :fontSize "2.5em"
    ;;               :placeSelf "start center"
    ;;               }}
    ;;  "Você é um profissional de Educação Física"
    ;;  [:br]
    ;;  "que preza pela qualidade de suas prescrições de treino."]
    [:h1
     {:style #js {:textAlign "center"
                  :color "white"
                  :gridArea "3 / 1 / 3 / 1"
                  :fontFamily "Mansalva, serif"
                  :fontSize "2.5em"
                  :maxWidth "900px"
                  :placeSelf "start center"}}
     "Você é um profissional de Educação Física que preza pela qualidade de suas prescrições de treino."]]
   [sessions-chart
    {:style #js {:height "33vh"
                 :width "97vw"
                 :placeSelf "center"}}]
   [macro-timeline
    {:style #js {:paddingTop "5px"
                 :placeSelf "center"
                 :width "97vw"
                 :height 220}}]
   [redefine-periodization]
   [:h2
    {:style #js {:paddingTop "180px"
                 :textAlign "center"}}
    "Na versão completa você também pode editar sessões de treino individualmente, alterar a modalidade da sessão, alterar microcíclos e analisar volume semanal."]
   (let [demo-idx (atom 1)]
     [:video
      {:src "video-demos/demo1.webm"
       :style #js {:maxWidth "850px"
                   :maxHeight "664px"
                   :placeSelf "center"
                   :height "calc(100vw * 0.86)"}
       :autoplay ""
       :muted ""
       :onLoadedData #(set! (-> % .-target .-playbackRate) 3)
       :onEnded #(do (set! (-> % .-target .-src)
                           (str "video-demos/demo"
                                (-> @demo-idx (mod 3) inc)
                                ".webm"))
                     (swap! demo-idx inc))}])
   [:iframe
    {:src "https://docs.google.com/forms/d/e/1FAIpQLSe6JU3ZycUlp_wpA_0vhSWhq8uK-A-BokKHsCeCGiuNBdeY1Q/viewform?embedded=true"
     :style #js {:placeSelf "center"
                 :width "650px"
                 :height "80vh"
                 :padding "20px 0 20px 0"}
     :frameborder 0
     :marginheight 0
     :marginwidth 0}
    "Carregando.."]
   [:b
    {:style #js {:padding "25px 20px"}}
    "Periodiza 2019 | contato@periodiza.com"]])
