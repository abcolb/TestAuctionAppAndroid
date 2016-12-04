/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.hubspot.auctionapp;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Item {

  public String name;
  public Integer openbid;
  public String donoremail;
  public String imageurl;
  public Integer qty;
  public String donorname;
  public String description;
  public Integer islive;
  // public Map<String, Object> bids;

  public Item() {
    // Default constructor required for calls to DataSnapshot.getValue(Item.class)
  }

  public String getImageurl() {
    return this.imageurl == null || this.imageurl.isEmpty() ? "http://cdn2.hubspot.net/hubfs/53/sprocket_web-2.png" : this.imageurl;
  }

}
