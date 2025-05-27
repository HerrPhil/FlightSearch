# FlightSearch

The Unit 6 final project.

## Overview


In this project, a Flight Search app is built in which users enter an airport
and can view a list fo destinations using that airport as a departure. This
project gives an opportunity to practice skills with SQL, Room, and DataStore by
offering a set of app requirements to fulfill. In particular, the Flight Search
app must meet the following requirements:

* Provide a text field for the user to enter an airport name or
  International Air Transport Association airport identifier.
* Query the database to provide autocomplete suggestions as the user types.
* When the user chooses a suggestion, generate a list of available flights
  from that airport, including IATA identifier and airport name to other
  airports in the database.
* Let the user save favourite individual routes.
* When no search query is entered, display all the user-selected favourite
  routes in a list.
* Save the search text with Preferences DataStore. When the user re-opens
  the app, the search text, if any, needs to prepopulate the text field
  with appropriate results from the database.

There is a pre-populated database for this project.

The expectation is the participant builds the app from scratch per the
requirements.

## The Search Field and Dropdown

I am working on the flight search app. The first component at the top of the
screen is a text search dropdown. I maintained an equivalent solution written in
2017, using reactive programming, like RxBinding/RxJava.

<quote>
The ExposedDropdownMenuBox from the Material3 library can be used to implement a
dropdown menu which facilitates the autocompletion process.
The actual autocompletion logic needs to be implemented manually, including
filtering the options based on the text field value and updating the selected
option when an item is chosen from the dropdown menu.
</unquote>

I know how complex it is the Java-flavoured search by text and filter a list
options.

Refer to this article: [RxBinding Search](https://riptutorial.com/android/example/16836/appcompat-searchview-with-rxbindings-watcher)

Note the developer must:
* set up the search view with its adapter, its search/click and close listeners,
  and RxSearchView builder pattern associated with the search view
* code a search adapter to implement the population of the cursor, filter the
  cursor, bind search results to the view.

I found the Jetpack Compose version of this feature much easier to code up. The
components that I composed to appear as one unit are the ExposedDropdownMenuBox,
the TextField, the ExposedDropdownMenu, and DropdownMenuItem. Values that are
treated as UI state are the expanded flag of the dropdown, the filter options,
and the text field value.

The only gotcha I encountered was learning what component is responsible for
toggling the expanded flag. My first guess was to include the toggle in the
onValueChange of the TextField. This broke the code on the edge case when the
user deletes all characters. The TextField lost focus and the background was
switched to a secondary color. Then I realized that the onExpandedChange of the
ExposedDropdownMenuBox is the only toggle for the expanded flag that is needed.

The only other issue is a UX issue. I included the trailingIcon (down/up arrows).
It automatically changes when the dropdown is expanded on value changes in the
text field. However, if the user manually clicks the trailing icon, this manual
event overrides the default, automated event.
