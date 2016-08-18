import React from 'react';



export default class ModalContent extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return <div className="modal-content">
            {this.props.children}
        </div>
    }
}