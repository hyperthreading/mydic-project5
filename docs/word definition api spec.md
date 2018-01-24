# Word Definition API Layer Specification

어학사전 API Layer의 요구 사항을 정리한 문서입니다.

네이버, 다음 어학 사전 기준으로 작성하였으며, 후에 변경 사항이 있을 수 있습니다.

시간이 날 때 clojure.spec으로 다시 작성할 수도 있습니다.

## data specification 

### api-capability

API로 이용할 수 있는 기능을 정의합니다

```clojure
{:completion true
 :search true
 :detailed-search true
 :definition true
 :usage true
 :related true}
```

### word-completion

입력한 단어에 대해 자동완성 후보를 나열합니다.

```clojure
{:query "commun"
 :list  [{:word "commune"
          :definition "123123"}]}
```

### word-link

해당 단어의 정의를 찾기 위해 사용하는 최소 단위의 링크입니다.

단어 검색 결과, 연관 단어 등에 쓰일 수 있습니다.

단어의 정의(definition)는 필수 사항이 아닙니다.

```clojure
{:word "community"
 :id "123123"
 :definition "커뮤니티"}
```

### word-definition

단어의 특정 의미를 나타내는 최소 단위입니다

단어의 품사를 verb, adverb, adjective 등으로 나타낼 수 있습니다.

품사의 더 자세한 특성은 transitive, intransitive 등으로 나타냅니다.

```clojure
{:class [:verb :transitive]
 :text "[사람·물건에서] [의무·책임·고통 등을] 면제하다, 면해주다[from ‥]"
 :usage [{:text ""
          :translation}]}
```

### usage

예문을 나타냅니다. 

반드시 어떤 단어에 대한 예문일 필요는 없습니다.

```clojure
{:text "The outcome of his condition .."
 :translation "그의 건강상태의 결과는.."}
```

### word-idiom

어떤 단어의 복합어, 관용적 표현의 최소 단위입니다.

 ```clojure
{:phrase "tax exempt"
 :meaning "면세의"}
 ```



### word-search

어떤 키워드에 대한 일반적인 검색 결과입니다.

단어의 정의, 예문, 연관 단어 등 여러가지 검색 결과를 포함할 수 있습니다.

```clojure
{:query  "commun"
 :types  [:definition :usage :related]
 :result {:definition [{:word       "community"
                        :id         "123123"
                        :definition "커뮤니티"}]
          :usage      [{:text        "community ewfwef"
                        :translation "번역 어쩌고 저쩌고"}]
          :related    [{:word       "commune"
                        :id         "123444"
                        :definition "코뮌"}]}}
```

### word-detailed-search

단어의 정의, 예문, 연관 단어 등에 대해 검색할 수 있습니다.

기본적으로 pagination을 지원합니다.

```clojure
{:query  "commun"
 :type   :definition
 :result [{:word "community"
           :id   "qwd"
           :definition "커뮤니티"}]
 :page 1}
```



### word-summary

어떤 단어에 대한 전반적인 정보와 자세한 정의를 나타냅니다.

definition은 반드시 포함되어야 하지만, definition-summary는 포함되지 않을 수 있습니다.

```clojure
{:word "exempt"
 
 :definition-summary ["면제하다" "면제된" "면해주다"]
 :definition [{:class [:verb :transitive]
               :text "[사람·물건에서] [의무·책임·고통 등을] 면제하다, 면해주다[from ‥]"
               :usage [{:text ""
                        :translation ""}]}]

 :pronounce {:us {:text      "igzémpt"
                  :sound-url "http://t1.daumcdn.net/language/4F7127E701683801B9"}
             :uk {:text      "igzémpt"
                  :sound-url "http://t1.daumcdn.net/language/4F714E2B012FD801F4"}}

 :usage [{:text        "The outcome of his condition .."
          :translation "그의 건강상태의 결과는.."}]
 
 :idiom [{:phrase  "tax exempt"
          :meaning "면세의"}]

 :related [{:word       "free"
            :id         "1233"
            :definition "자유의"}]}
```





## functions

### api-capability

### word-completition

### word-search-result

### word-definition

word -> word-definition

### word-usage

### word-related