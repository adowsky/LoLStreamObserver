import React from 'react';
import ContentTableView from './ContentTableView.jsx'
import TableHeader from './components/TableHeader.jsx';
import TableData from './components/TableData.jsx';

export default class ContentTable extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return <ContentTableView>
            <TableHeader />
            <TableData />
        </ContentTableView>
    }
}