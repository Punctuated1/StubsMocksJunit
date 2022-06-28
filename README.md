# StubsMocksJunit
Junit Testing with Stubs and Mocks in Java

<div class=WordSection1>

<p class=MsoNormal style='margin-bottom:0in;line-height:normal'><b><span
style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Our SUT is an event
scheduler. </span></b><span style='mso-ascii-font-family:Calibri;mso-fareast-font-family:
"Times New Roman";mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'><o:p></o:p></span></p>

<p class=MsoNormal style='margin-bottom:0in;line-height:normal'><span
style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The SUT accepts an
input object that defines an event to be scheduled<o:p></o:p></span></p>

<p class=MsoNormal style='margin-bottom:0in;line-height:normal'><span
style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>&nbsp;<o:p></o:p></span></p>

<p class=MsoNormal style='margin-bottom:0in;line-height:normal'><span
style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The input object
contains a description of the event, and email distribution list, a point of
contact email address for the event coordinator, the venue for the event, a
payment instrument to pay deposit on the venue, amount of the deposit; and a
number of business days in the future the event is to be scheduled on.<o:p></o:p></span></p>

<p class=MsoNormal style='margin-bottom:0in;line-height:normal'><span
style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>&nbsp;<o:p></o:p></span></p>

<ol style='margin-top:0in' start=1 type=1>
 <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:l0 level1 lfo1;
     tab-stops:list .5in;vertical-align:middle'><span style='mso-ascii-font-family:
     Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:
     Calibri;mso-bidi-font-family:Calibri'>The SUT calls a SOAP web service to
     verify the selected venue for the event meets the requirements for the
     event:<o:p></o:p></span></li>
 <ul style='margin-top:0in' type=circle>
  <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
      l0 level2 lfo1;tab-stops:list 1.0in;vertical-align:middle'><span
      style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
      mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>We test
      expectations for:<o:p></o:p></span></li>
  <ul style='margin-top:0in' type=disc>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The crowd
       capacity for the venue is greater than the expected attendance<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The venue
       includes a large screen backdrop on stage for slides and videos<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>An exception
       is thrown by the SOAP access attempt<o:p></o:p></span></li>
  </ul>
 </ul>
 <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:l0 level1 lfo1;
     tab-stops:list .5in;vertical-align:middle'><span style='mso-ascii-font-family:
     Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:
     Calibri;mso-bidi-font-family:Calibri'>The SUT calls a collaborator utility
     that is used by several other services that accepts a number representing
     business days in the future<o:p></o:p></span></li>
 <ul style='margin-top:0in' type=circle>
  <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
      l0 level2 lfo1;tab-stops:list 1.0in;vertical-align:middle'><span
      style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
      mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The
      collaborator performs boundary checks on the input number. If the input
      number fails any of the boundary checks it throws a <span class=SpellE>InputException</span>,
      or a subtype of <span class=SpellE>InputException</span><o:p></o:p></span></li>
  <ul style='margin-top:0in' type=disc>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The number
       must be greater that zero<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The number
       cannot be greater than 30<o:p></o:p></span></li>
  </ul>
  <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
      l0 level2 lfo1;tab-stops:list 1.0in;vertical-align:middle'><span
      style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
      mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>If the number
      passes boundary validations it makes one or more calls to an external
      dependency, a database that contains our corporate calendar - this is a
      relatively slow process and we want to process over <span class=GramE>10,000
      unit</span> tests in under 5 minutes so we decide to use a Test Double,
      since this is a share utility several of our test cases will utilize we
      elect to build s custom stub<o:p></o:p></span></li>
  <ul style='margin-top:0in' type=disc>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>We model our
       stub modeled on November 2028, where the base date the business days in
       future are computed from is October 31<o:p></o:p></span></li>
   <ul style='margin-top:0in' type=square>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l0 level4 lfo1;tab-stops:list 2.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>There are
        two holidays - our simple calendar has 22 and 23 for Thanksgiving<o:p></o:p></span></li>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l0 level4 lfo1;tab-stops:list 2.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Weekends
        are 4-5, 11-12, 18-19, 25 - 26, and 2-3 and 9-10 in next month<o:p></o:p></span></li>
   </ul>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo1;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The stub
       returns the date that is the specified number of business days from our
       base date (10-31-2028)<o:p></o:p></span></li>
  </ul>
 </ul>
 <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:l0 level1 lfo1;
     tab-stops:list .5in;vertical-align:middle'><span style='mso-ascii-font-family:
     Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:
     Calibri;mso-bidi-font-family:Calibri'>The SUT next calls a REST web
     service to verify the venue is available for the planned date and reserve
     it if available with a deposit<o:p></o:p></span></li>
</ol>

<ol style='margin-top:0in' start=3 type=1>
 <ul style='margin-top:0in' type=circle>
  <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
      l0 level2 lfo2;tab-stops:list 1.0in;vertical-align:middle'><span
      style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
      mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>We test
      expectations for:<o:p></o:p></span></li>
 </ul>
</ol>

<ol style='margin-top:0in' start=3 type=1>
 <ul style='margin-top:0in' type=circle>
  <ul style='margin-top:0in' type=disc>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Date is
       available and reservation is successful<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Date is not
       available and reservation is denied<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Connection
       fails<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Invalid
       credit card<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Expired
       credits card<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Invalid CVC<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Deposit
       amount exceeds available credit, transaction denied<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Credit
       approved<o:p></o:p></span></li>
  </ul>
 </ul>
 <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:l0 level1 lfo3;
     tab-stops:list .5in;vertical-align:middle'><span style='mso-ascii-font-family:
     Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:
     Calibri;mso-bidi-font-family:Calibri'>The SUT then creates a database
     record for the event <o:p></o:p></span></li>
 <ul style='margin-top:0in' type=circle>
  <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
      l0 level2 lfo3;tab-stops:list 1.0in;vertical-align:middle'><span
      style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
      mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>The database
      call is relatively slow and we elect to mock the database with Mockito<o:p></o:p></span></li>
  <ul style='margin-top:0in' type=disc>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>We set
       expectations for: <o:p></o:p></span></li>
   <ul style='margin-top:0in' type=square>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l0 level4 lfo3;tab-stops:list 2.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Successful
        persistence of the appointment<o:p></o:p></span></li>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l0 level4 lfo3;tab-stops:list 2.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Failed
        persistence of the appointment due to illegal argument exception<o:p></o:p></span></li>
   </ul>
  </ul>
 </ul>
 <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:l0 level1 lfo3;
     tab-stops:list .5in;vertical-align:middle'><span style='mso-ascii-font-family:
     Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:
     Calibri;mso-bidi-font-family:Calibri'>Finally, we send an email invitation
     to the event to our distribution list - we do not want to actually send
     emails and this is our only email process so we use Mockito to create a Test
     Double<span style='mso-spacerun:yes'>  </span><o:p></o:p></span></li>
 <ul style='margin-top:0in' type=circle>
  <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
      l0 level2 lfo3;tab-stops:list 1.0in;vertical-align:middle'><span
      style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
      mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>We set
      expectations that test:<o:p></o:p></span></li>
  <ul style='margin-top:0in' type=disc>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Email
       address list is not valid,<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Sender is
       not valid<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Subject is
       missing<o:p></o:p></span></li>
   <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
       l0 level3 lfo3;tab-stops:list 1.5in;vertical-align:middle'><span
       style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
       mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Subject is
       too long<o:p></o:p></span></li>
  </ul>
 </ul>
</ol>

<p class=MsoNormal style='margin-top:0in;margin-right:0in;margin-bottom:0in;
margin-left:1.5in;line-height:normal;vertical-align:middle'><span
style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'><o:p>&nbsp;</o:p></span></p>

<table class=MsoNormalTable border=1 cellspacing=0 cellpadding=0 title=""
 summary="" style='border-collapse:collapse;border:none;mso-border-alt:solid #A3A3A3 1.0pt;
 mso-yfti-tbllook:1184;mso-padding-alt:0in 0in 0in 0in'>
 <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes'>
  <td width=897 valign=top style='width:672.9pt;border:solid #A3A3A3 1.0pt;
  background:#D9E2F3;mso-background-themecolor:accent1;mso-background-themetint:
  51;padding:4.0pt 4.0pt 4.0pt 4.0pt'>
  <p class=MsoNormal style='margin-bottom:0in;line-height:normal'><b><span
  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
  mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri;color:black;
  mso-color-alt:windowtext'>Thin Slices Tests to implement:</span></b><span
  style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
  mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'><o:p></o:p></span></p>
  </td>
 </tr>
 <tr style='mso-yfti-irow:1;mso-yfti-lastrow:yes'>
  <td width=899 valign=top style='width:674.0pt;border:solid #A3A3A3 1.0pt;
  border-top:none;mso-border-top-alt:solid #A3A3A3 1.0pt;padding:4.0pt 4.0pt 4.0pt 4.0pt'>
  <ul style='margin-top:0in' type=disc>
   <ol style='margin-top:0in' start=1 type=1>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l1 level2 lfo4;tab-stops:list 1.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>SOAP <span
        class=SpellE>WS</span> with Spring <span class=SpellE>MockWebServiceServer</span><o:p></o:p></span></li>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l1 level2 lfo4;tab-stops:list 1.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Shared
        collaborator as a custom stub<o:p></o:p></span></li>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l1 level2 lfo4;tab-stops:list 1.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Rest API
        with Spring <span class=SpellE>MockRestServiceServer</span><o:p></o:p></span></li>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l1 level2 lfo4;tab-stops:list 1.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Database
        dependency with Mockito<o:p></o:p></span></li>
    <li class=MsoNormal style='margin-bottom:0in;line-height:normal;mso-list:
        l1 level2 lfo4;tab-stops:list 1.0in;vertical-align:middle'><span
        style='mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
        mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri'>Non-shared
        collaborator with Mockito<o:p></o:p></span></li>
   </ol>
  </ul>
  </td>
 </tr>
</table>

<p class=MsoNormal><o:p>&nbsp;</o:p></p>

</div>
