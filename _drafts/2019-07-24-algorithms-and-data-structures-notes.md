---
layout: post
title: 2019-07-24-algorithms-and-data-structures-notes
date: 2019-07-24 07:00:00 +0000
tags: [algorithms, data-structures]
---

## Intro


## Algorithms

### Big O

In simple terms represents the worst case scenario for executing an operation on a data structure, related to the length n of the data structure.

|Operation |Array |List |
|---|---|---
|Insert |O(n) |O(1)
|Delete |O(1) |O(n)
|Random read |O(1) |O(n) 
|Sequential read |O(1) | O(1)

## Binary Search

### Input:a sorted array

### Output: item (if found) or null/nil

### Worst case scenario O(log n)

### Instructions 

Start from the middle, compare your element with the two halfs. Pick the half  that contains the element and repeat splitting in half, then comparing until you are left with your element. 

## Data Structures

* Contiguously-allocated structures are composed of single slabs of memory, and include arrays, matrices, heaps, and hash tables.
* Linked data structures are composed of distinct chunks of memory bound together by pointers, and include lists, trees, and graph adjacency lists.

### Array

The array is the fundamental contiguously-allocated data structure.

* A contiguous block of memory 
* Indexing starts from 0

#### Advantages of contiguously-allocated arrays include:

* Constant-time access given the index
* Space efficiency
* Memory locality (take advantage of the cache-memory)
* Guarantee that each array-access takes constant time in the worst case.

#### Disadvantages of arrays:

* Cannot be resized once they have been allocated

#### Java Arrays

```java

static void sort(Object[] arr) - uses mergesort, all elements must implement the Comparable interface
static void sort(int[] arr) - uses Dual-Pivot QuickSort, which performs in O(n*lg(n))
static int binarySearch(long[] a, long key) - the array must be sorted
```

### Linked List

* Non-contiguous blocks of memory
* One element links to the next
* Has the concept of `head` and `tail`
* Head represents the first element
* Tail represents the list without the head

### Heap

A heap is a data structure that satises the following heap property: 

* Top operation always returns the minimum (maximum) element
* Pop operation removes the top element from the heap while the heap property should be kept, so that the new top element is still the minimum (maximum) one
* Insert a new element to heap should keep the heap property. That the new top is still the minimum (maximum) element
* Other operations including merge etc. should all keep the heap property

This is a kind of recursive defition, while it doesn't limit the under ground data structure.
We call the heap with the minimum element on top as `min-heap`, while if the top keeps the maximum element, we call it `max-heap`.

#### Java Collections Framework

* Allows to sort elements of a list with `Collections.sort(list)` or `Collections.sort(list, comparator)`.
* Reference type for `Set` and keys of `Map` must be immutable
* Override `hashCode` and `equals` for Map and Set

##### Collection

##### Set

##### SortedSet

### List

An ordered Collection that allows duplicates. Java’s default reference types implement Comparable and use a natural sorting order. Lists of any of these types can be sorted using Collections.sort(list), for a given List<T> list. If you want to sort a list of custom reference type, then your type needs to implement Comparable interface. For elements who do not implement Comparable or do not want to use natural ordering, you can provide a Comparator.

They provide:

* Positional access: get, set, add, remove
* Search: indexOf, lastIndexOf
* Iteration: listIterator
* Range-view: perform arbitrary range operations

sort() - sorts a List using a merge sort algorithm
binarySearch() - searches for an element in an ordered List using the binary search algorithm

#### ArrayList (not syncronized)

Resizable-array implementation of the List interface.

Asymptotic running time:

* O(1) for: size, isEmpty, get, set, iterator, and listIterator.
* Add runs in amortised constant time, where inserting n elements takes O(n) time.
* All of the other operations run in linear time (roughly speaking).
* Can be sorted with sort(Comparator<? Super E> c), where elements must be mutually comparable.

#### LinkedList (not syncronized)

Doubly-linked list implementation of the List and Deque interfaces.

#### Queue

A collection for holding elements prior to processing.

#### Dequeue

Usually pronounced as deck, a deque is a double-ended-queue. A double-ended-queue is a linear collection of elements that supports the insertion and removal of elements at both end points. The Deque interface is a richer abstract data type than both Stack and Queue because it implements both stacks and queues at the same time.

### Map

A Map is an object that maps keys to values. A map cannot contain duplicate keys: Each key can map to at most one value. It models the mathematical function abstraction.

#### SortedMap

Pointers and Linked Structures
Pointers are the connections that hold the pieces of linked structures together.
The list is the simplest linked structure.

#### Comparison

Relative advantages of linked lists over static arrays:

* Overflow on linked structures can never occur unless the memory is actually full.
* Insertions and deletions are simpler than for contiguous (array) lists.
* With large records, moving pointers is easier and faster than moving the items themselves.

Relative advantages of arrays:

* Linked structures require extra space for storing pointer fields.
* Linked lists do not allow efficient random access to items.
* Arrays allow better memory locality and cache performance than random pointer jumping.

`Take-Home Lesson: Dynamic memory allocation provides us with flexibility on how and where we use our limited storage resources.`

* Lists – Chopping the first element off a linked list leaves a smaller linked list. This same argument works for strings, since removing characters from string leaves a string. Lists are recursive objects.
* Arrays – Splitting the first k elements off of an n element array gives two smaller arrays, of size k and n−k, respectively. Arrays are recursive objects.

### Stacks and Queues

We use the term container to denote a data structure that permits storage and retrieval of data items independent of content.

* Stacks – Support retrieval by last-in, first-out (LIFO) order: push(x, s), pop(s). Algorithmically, LIFO tends to happen in the course of executing recursive algorithms.

* Queues – Support retrieval in first in, first out (FIFO) order: enqueue(x, q), dequeue(q), peak(q). The fundamental data structure controlling breadth-first searches in graphs.

Stacks and queues can be effectively implemented using either arrays or linked lists. The key issue is whether an upper bound on the size of the container is known in advance.

### Dictionary

The dictionary data type permits access to data items by content. Search(D, k), Insert(D, x), Delete(D, x). Certain dictionary data structures also support: Min(D), Max(D), Predecessor(D, k), Successor(D, k).
